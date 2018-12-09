package com.cyf.team.entity

import android.util.Log

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/8/21/021.
 */
const val TEST_DEVICE_ID = "TR-005D16660016402020E42DE4"

//const val INTERFACE_INDEX = "/api.php/Index/"
//const val FROM = "/from/android"
//const val KEY_STR = "/keystr/"
//
//fun String.getImageUrl(): String {
//    return if (this.contains("http")) this else ROOT_URL + "/" + this
//}
//
fun String.getInterface(): String {
    return ROOT_URL + this + CLIENT + KEYSTR
}
//
//fun getKeyStr(jsonStr: String, inter: String): String {
//    return md5(jsonStr + "nimdaae" + inter + CalendarUtil(System.currentTimeMillis()).format(CalendarUtil.YYYYMMDD))
//}
//
//private fun md5(string: String): String {
//    if (TextUtils.isEmpty(string)) {
//        return ""
//    }
//    val md5: MessageDigest = MessageDigest.getInstance("MD5")
//    val bytes = md5.digest(string.toByteArray())
//    var result = ""
//    val c = 0xff
//    for (i in bytes.indices) {
//        var temp = Integer.toHexString(c and bytes[i].toInt())
//        if (temp.length == 1) {
//            temp = "0$temp"
//        }
//        result += temp
//    }
//    return result
//}

const val LeZuInfoUrl = "http://www.lovelezu.com/index.php?s=/News/index.html"

const val IMAGE_URL = "http://weixin.lovelezu.com/"

const val ROOT_URL = IMAGE_URL + "kucun.php/Index/"

const val CLIENT = "/from/android"

const val KEYSTR = "/keystr/defualtencryption"

const val LOGIN = "login"//Boss登陆

const val JWD = "jwd"//上报经纬度

const val CV = "cv" //App版本

const val KUCUN_TYPE = "kucuntype"//产品分类

const val KUCUN_LISTS = "kucunlists" //产品列表

const val SENDMSG = "sendmsg" //发送短信验证码

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

const val CACCOUNT = "caccount" //更换员工手机号码（店铺）


