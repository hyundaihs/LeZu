package com.cyf.union

import android.app.Application
import com.cyf.lezu.E
import com.cyf.lezu.entity.APP_ID
import com.cyf.lezu.entity.SystemInfo
import com.cyf.lezu.entity.WorkerInfo
import com.cyf.lezu.utils.AppPath
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.uuzuche.lib_zxing.activity.ZXingLibrary
import kotlin.properties.Delegates

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/1/21/021.
 */

class AppUnion : Application() {

    val api: IWXAPI by lazy {
        WXAPIFactory.createWXAPI(this, APP_ID, false)
    }

    companion object {
        var logier_id: UserID? = UserID.WORKER//员工,BOSS?
        var isLogged: Boolean = false //是否登录
        lateinit var systemInfo: SystemInfo //系统信息
        var instance: AppUnion by Delegates.notNull()
        var workerList = ArrayList<WorkerInfo>()
        var edu: Int = 0
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        ZXingLibrary.initDisplayOpinion(this)
        if (api.registerApp(APP_ID)) {
            E("注册微信成功")
        } else {
            E("注册微信失败")
        }
    }
}

enum class UserID {
    WORKER, BOSS
}
