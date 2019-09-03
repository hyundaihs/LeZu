package com.cyf.team

import android.app.Application
import cn.jpush.android.api.JPushInterface
import com.baidu.mapapi.SDKInitializer
import com.cyf.lezu.E
import com.cyf.lezu.entity.APP_ID
import com.cyf.lezu.entity.SystemInfo
import com.cyf.lezu.entity.WorkerInfo
import com.cyf.team.entity.CargoDetails
import com.cyf.team.entity.CreditInfo
import com.cyf.team.entity.UserInfo
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.uuzuche.lib_zxing.activity.ZXingLibrary
import kotlin.properties.Delegates

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/1/001.
 */
class AppTeam : Application() {

    val api: IWXAPI by lazy {
        WXAPIFactory.createWXAPI(this, APP_ID, false)
    }

    companion object {
        var logier_id: Int = UserID.KU_GUAN
        var isLogged: Boolean = false //是否登录
        var instance: AppTeam by Delegates.notNull()
        var creditInfo: CreditInfo by Delegates.notNull()
        var userInfo: UserInfo by Delegates.notNull()
        var cargoDetails : CargoDetails by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)
        ZXingLibrary.initDisplayOpinion(this)
        SDKInitializer.initialize(applicationContext);
        if (api.registerApp(APP_ID)) {
            E("注册微信成功")
        } else {
            E("注册微信失败")
        }
    }
}

class OrderListType {
    companion object {
        const val KG_MISSION_OUT = 0//库管-最新任务-出库
        const val KG_MISSION_IN = 1 //库管-最新任务-入库
        const val KG_ORDER_ALL = 2 //库管-订单-全部
        const val KG_ORDER_WAIT_CONFIRM = 3 //库管-订单-全部
        const val KG_ORDER_CONFIRMED = 4 //库管-订单-待确认
        const val KG_ORDER_SEND = 5 //库管-订单-已发货
        const val KG_ORDER_CANCEL = 6 //库管-订单-已取消
        const val KG_ORDER_IN = 7 //库管-订单-已入库
        const val XJ_ORDER_OUT = 8 //巡检-订单-出库
        const val XJ_ORDER_IN = 9 //巡检-订单-入库
        const val GR_ORDER_OUT = 10 //巡检-订单-出库
        const val GR_ORDER_IN = 11 //巡检-订单-入库
        const val KG_HISTORY_OUT = 12//库管-历史订单-出库
        const val KG_HISTORY_IN = 13 //库管-历史订单-入库
    }
}

class UserID {
    companion object {
        const val KU_GUAN = 6
        const val XUN_JIAN = 7
        const val GONG_REN = 8
    }
}