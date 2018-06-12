package com.cyf.lezu.requests

import android.content.Context
import com.cyf.lezu.D
import com.cyf.lezu.entity.RequestResult
import com.google.gson.Gson
import com.squareup.okhttp.*
import com.squareup.okhttp.Request
import java.io.IOException
import com.squareup.okhttp.RequestBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.CookieStore
import java.util.concurrent.TimeUnit


/**
 * LeZu
 * Created by 蔡雨峰 on 2018/3/26.
 */

public class MySimpleRequest(var callback: RequestCallBack) {


    private val mOkHttpClient: OkHttpClient by lazy {
        OkHttpClient()
    }

    companion object {
        var sessionId: String = ""

        private val MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8")//mdiatype 这个需要和服务端保持一致

        const val IMAGE_URL = "http://weixin.lovelezu.com/"

        const val ROOT_URL = IMAGE_URL + "api.php/Index/"

        const val CLIENT = "/from/android"

        const val KEYSTR = "/keystr/defualtencryption"

        const val LOGINERR = "loginerr"//需要重新登录错误信息

        const val SYS_INFO = "sysinfo" //获取系统信息

        const val SENDMSG = "sendmsg" //发送短信验证码

        const val LOGIN = "login"//Boss登陆

        const val LOGINYG = "loginyg"//员工登陆

        const val VERF_LOGIN = "verflogin"//Boss登陆验证码登陆

        const val VERF_LOGIN_YG = "verfloginyg"//员工登陆验证码登陆

        const val LOG_OUT = "logout"//退出登陆

        const val ZT_INFO = "ztinfo"//店铺详情

        const val YG_LISTS = "yglists"//员工列表

        const val QDXQ = "qdxq"//签到详情

        const val YGINFO = "yginfo"//签到详情

        const val YGJX = "ygjx"//员工绩效详情（店铺）

        const val ZT_INDEX = "ztindex"//店铺首页详情(员工)

        const val ZT_ACCOUNT_LISTS = "ztaccountlists"//店铺会员列表

        const val ZT_ACCOUNT_LISTS_YG = "ztaccountlistsyg"//员工会员列表

        const val FF_YHQ = "ffyhq"//店铺发放优惠券

        const val FF_YHQ_YG = "ffyhqyg"//员工发放优惠券

        const val YHQ_LISTS = "yhqlists"//店铺优惠券列表

        const val YHQ_LISTS_YG = "yhqlistsyg"//员工优惠券列表

        const val QD = "qd"//员工签到

        const val ORDER_INFO = "orderinfo"//订单详情

        const val TXSQ = "txsq"//余额提现

        const val REG = "reg"//员工注册

        const val FPASS = "fpass"//找回密码

        const val YGJXYG = "ygjxyg" // 员工绩效详情（员工）

        const val QDJX = "qdjx" //确定绩效（员工）

        const val FFJX = "ffjx" //店铺发放绩效给员工

        const val XGJX = "xgjx" //修改订单绩效金额

    }

    fun getRequest(context: Context, zipCode: String) {
        context.doAsync {
            val request = Request.Builder().url(ROOT_URL + zipCode + CLIENT + KEYSTR).addHeader("cookie", sessionId).build()
            val call = mOkHttpClient.newCall(request)
            //请求加入调度
            val response = call.execute()
            if (response.isSuccessful) {
                val string = response.body().string()
                getSession(response)
                val res = Gson().fromJson(string, RequestResult::class.java)
                if (res.retInt == 1) {
                    uiThread {
                        callback.onSuccess(string)
                    }
                } else {
                    if (res.retErr == LOGINERR) {
                        uiThread {
                            callback.onLoginErr()
                        }
                    } else {
                        uiThread {
                            callback.onError(res.retErr)
                        }
                    }
                }
            } else {
                uiThread {
                    callback.onError(response.message())
                }
            }
        }
    }

    fun postRequest(context: Context, zipCode: String, map: Map<String, String>) {
        context.doAsync {
            val requestBody = RequestBody.create(MEDIA_TYPE_JSON, Gson().toJson(map))
            val request = Request.Builder().url(ROOT_URL + zipCode + CLIENT + KEYSTR).post(requestBody).addHeader("cookie", sessionId).build()
            val response = mOkHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val string = response.body().string()
                D("postRequest " + string)
                getSession(response)
                val res: RequestResult = Gson().fromJson(string, RequestResult::class.java)
                D(res.toString())
                if (res.retInt == 1) {
                    uiThread {
                        callback.onSuccess(string)
                    }
                } else {
                    if (res.retErr == LOGINERR) {
                        uiThread {
                            callback.onLoginErr()
                        }
                    } else {
                        uiThread {
                            callback.onError(res.retErr)
                        }
                    }
                }
            } else {
                uiThread {
                    callback.onError(response.message())
                }
            }
        }
    }

    private fun getSession(response: Response) {
        val headers = response.headers()
        val cookies = headers.values("Set-Cookie")
        if (cookies.isEmpty()) {
            return
        }
        val session = cookies[0]
        sessionId = session.substring(0, session.indexOf(";"))
    }

    class MyCallBack(private val callback: RequestCallBack) : Callback {
        override fun onFailure(request: Request?, e: IOException) {
            callback.onError(e.message.toString())
        }

        override fun onResponse(response: Response) {
            val string = response.body().string()
            val res = Gson().fromJson(string, RequestResult::class.java)
            if (res.retInt == 1) {
                callback.onSuccess(string)
            } else {
                if (res.retErr == LOGINERR) {
                    callback.onLoginErr()
                } else {
                    callback.onError(res.retErr)
                }
            }
        }

    }

    interface RequestCallBack {
        fun onSuccess(result: String)
        fun onError(error: String)
        fun onLoginErr()
    }

}