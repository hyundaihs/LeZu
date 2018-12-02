package com.cyf.union.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.initActionBar
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.CustomDialog
import com.cyf.lezu.utils.LoginErrDialog
import com.cyf.union.R
import com.cyf.union.entity.QD
import com.cyf.union.entity.getInterface
import kotlinx.android.synthetic.main.activity_location.*


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/4/30/030.
 */
class LocationActivity : MyBaseActivity() {

    val mLocationClient: LocationClient by lazy {
        LocationClient(this)
    }

    private val myListener = MyLocationListener()
    //BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口
//原有BDLocationListener接口暂时同步保留。具体介绍请参考后文中的说明

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        initActionBar(this, "签到", rightBtn = "提交", rightClick = {
            if (checkEdits()) {
                submit()
            }
        })
        location_check.setOnClickListener {
            location()
        }
    }

    init {
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener)
        //注册监听函数
        val option = LocationClientOption()

        option.setIsNeedAddress(true)
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true

        mLocationClient.locOption = option;
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    }

    private fun location() {
        mLocationClient.start()
    }

    private fun checkEdits(): Boolean {
        when {
            location_info.text.toString().trim().isEmpty() -> location_info.error = "定位信息不能为空"
            else -> return true
        }
        return false
    }

    private fun submit() {
        val map = mapOf(Pair("address", location_info.text.toString()),
                Pair("contents", location_beizhu.text.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                CustomDialog(message = "签到成功", positiveClicked = DialogInterface.OnClickListener { _, _ ->
                    finish()
                })
            }

            override fun onError(context: Context, error: String) {
                toast(error)
                CustomDialog("错误", message = error)
            }

            override fun onLoginErr(context: Context) {
                LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this@LocationActivity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(this, QD.getInterface(), map)
    }

    inner class MyLocationListener : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

//            val addr = location.addrStr    //获取详细地址信息
//            val country = location.country    //获取国家
//            val province = location.province    //获取省份
//            val city = location.city    //获取城市
//            val district = location.district    //获取区县
//            val street = location.street    //获取街道信息
            location_info.text = location.addrStr
        }
    }
}