package com.cyf.team.fragments

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.cyf.lezu.fragments.BaseFragment
import com.cyf.lezu.initActionBar
import com.cyf.team.R
import kotlinx.android.synthetic.main.fragment_mine_gr.*
import com.baidu.location.LocationClientOption.LOC_SENSITIVITY_HIGHT
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.map.MyLocationData
import com.cyf.lezu.D
import com.cyf.lezu.entity.LoginInfoRes
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.team.AppTeam
import com.cyf.team.entity.JWD
import com.cyf.team.entity.LOGIN
import com.cyf.team.entity.getInterface
import com.google.gson.Gson


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/1/001.
 */
class MineGRFragment : BaseFragment() {

    private var isFirstLoc = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mine_gr, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.initActionBar(activity as AppCompatActivity, "实时定位", false)
        mMapView.map.isMyLocationEnabled = true
        initLocationOption()
    }

    /**
     * 初始化定位参数配置
     */

    private fun initLocationOption() {
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        val locationClient = LocationClient(activity)
        //声明LocationClient类实例并配置定位参数
        val locationOption = LocationClientOption()
        val myLocationListener = MyLocationListener()
        //注册监听函数
        locationClient.registerLocationListener(myLocationListener)
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll
        locationOption.setCoorType("gcj02")
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000)
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true)
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true)
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false)
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.isLocationNotify = true
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true)
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true)
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true)
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false)
        //可选，默认false，设置是否开启Gps定位
        locationOption.isOpenGps = true
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false)
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode()
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT)
        //开始定位
        localBtn.setOnClickListener {
            if (localBtn.text.toString() == "开启连续定位") {
                locationClient.start()
                localBtn.text = "关闭连续定位"
            } else {
                locationClient.stop()
                localBtn.text = "开启连续定位"
            }
        }
    }

    /**
     * 实现定位回调
     */
    inner class MyLocationListener : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            val latitude = location.getLatitude()
            //获取经度信息
            val longitude = location.getLongitude()
            //获取定位精度，默认值为0.0f
            val radius = location.getRadius()
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            val coorType = location.getCoorType()
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            val errorCode = location.getLocType()

            val locData = MyLocationData.Builder()
                    .accuracy(location.radius)
                    .direction(location.direction).latitude(location.latitude)
                    .longitude(location.longitude).build()

            // 设置定位数据
            mMapView.map.setMyLocationData(locData)

            //地图SDK处理
            if (isFirstLoc) {
                isFirstLoc = false
                val ll = LatLng(location.latitude,
                        location.longitude)
                val builder = MapStatus.Builder()
                builder.target(ll).zoom(18.0f)
                mMapView.map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
            }
            val point = LatLng(location.latitude, location.longitude)
            val dotOption = DotOptions().center(point).color(-0x55010000)
            mMapView.map.addOverlay(dotOption)

            latitudeText.text = "纬度：${latitude}"
            longitudeText.text = "经度：${longitude}"
            D("纬度：${latitude}  经度：${longitude}")

            submit(latitude, longitude)
        }
    }

    private fun submit(latitude: Double, longitude: Double) {
        val map = mapOf(Pair("lat", latitude.toString())
                , Pair("lng", longitude.toString())
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                activity?.toast("纬度：${latitude}  经度：${longitude}")
            }

            override fun onError(context: Context, error: String) {
            }

            override fun onLoginErr(context: Context) {
            }

        }, false).postRequest(activity as Context, JWD.getInterface(), map)
    }


    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }
}