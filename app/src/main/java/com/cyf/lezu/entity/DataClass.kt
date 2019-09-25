package com.cyf.lezu.entity

val APP_ID = "wx51aed9df5c7523f3"

/**
 * LeZu
 * Created by 蔡雨峰 on 2018/3/26.
 */

open class RequestResult(val retInt: Int = 0, val retErr: String = "", val retCounts: Int = 0) {
    override fun toString(): String {
        return "RequestResult(retInt=$retInt, retErr='$retErr', retCounts=$retCounts)"
    }
}

/**
 * [title] => 系统名称
[link_man] => 联系电话
[telphone] => 客服电话
[address] => 地址
[company_right] => 版权
 */
data class SystemInfo(val title: String, val link_man: String, val telphone: String, val address: String, val company_right: String)

data class SystemInfoRes(val retRes: SystemInfo) : RequestResult()

/*
 [login_verf] => 登录验证码（保存app本地，用于自动登录）
 */
data class LoginInfo(val login_verf: String)

data class LoginInfoRes(val retRes: LoginInfo) : RequestResult()

/**
 * [id] => 店铺ID
[account] => 账号
[ye_price] => 余额
[ewm_file_url] => 二维码地址(前面需加域名)
[login_time] => 登录时间（时间戳）
[file_url] => 头像
[title] => 名称
[hpl] => 100（好评率）
[address] => 地址
[app_contents] => 店铺介绍（html）
[yhqff] => 本年优惠券已发放金额
syyhq => 剩余优惠券额度
 */
data class StoreInfoBoss(val id: Int, val account: String, val ye_price: Double, val ewm_file_url: String,
                         val login_time: Long, val file_url: String, val title: String, val hpl: Int, val address: String,
                         val app_contents: String, val yhqff: Double, val syyhq: Double, val yhqqx: Int)

data class StoreInfoBossRes(val retRes: StoreInfoBoss) : RequestResult()

/**
 * [id] => id
[title] => 名称
[phone] => 电话
[orders_num] => 订单数量
[orders_price] => 订单金额
 */
data class WorkerInfo(val id: Int, val title: String, val phone: String, val orders_num: Int, val orders_price: Double)

data class WorkerListRes(val retRes: List<WorkerInfo>) : RequestResult()

/**
 *  [info] => Array
(
[id] => 员工ID
[zt_id] => 专题ID
[account] => 账号
[orders_num] => 订单数
[orders_price] => 订单金额
[qdl] => 月签到率
)

[lists] => Array（签到列表）
(
[周(0为周日)] => Array（未签到时为空数组）
(

[contents] => 备注
[address] => 地址
[year] => 年
[month] => 月
[day] => 日
[week] => 周（星期天为0）
)

)
 */
data class CheckWorkInfo(val id: Int, val zt_id: Int, val account: String, val orders_num: Int, val orders_price: Double, val qdl: String)

data class CheckWeekTable(val contents: String, val address: String, val year: Int, val month: Int, val day: Int, val week: Int)

data class CheckInfo(val info: CheckWorkInfo, val lists: List<CheckWeekTable>)

data class CheckInfoRes(val retRes: CheckInfo) : RequestResult()

/**
 *  [id] => 员工ID
[title] => 姓名
[zt_id] => 店铺ID
[account] => 账号
[ye_price] => 余额
[ewm_file_url] => 二维码地址(前面需加域名)
[login_time] => 登录时间（时间戳）
[file_url] => 头像
[title] => 名称
[yffjx] => 已发放绩效金额
syyhq => 剩余优惠券额度
 */
data class WorkerDetails(val id: Int, val title: String, val zt_id: Int, val account: String, val ye_price: Double,
                         val ewm_file_url: String, val login_time: Long, val file_url: String, val yffjx: Double,
                         val syyhq: Double, val yhqqx: Int,val yhqff: Double)

data class WorkerDetailsRes(val retRes: WorkerDetails) : RequestResult()

/**
 * [id] => 订单ID
[account] => 会员账号
[goods_num] => 商品数量
[price] => 订单成交价
[type] => 组购类型 （zl：租赁，gm：购买)
 */
data class GradeDetails(val id: Int, val account: String, val goods_num: Int, val price: Double, val type: String)

data class GradeDetailsListRes(val retRes: List<GradeDetails>) : RequestResult()

/**
 * info
 *[id] => 店铺ID
[title] => 名称
[address] => 地址
 */
data class StoreInfoWorker(val id: Int, val title: String, val address: String)

/**
 *[file_url] => 图片地址
[contents] => 介绍详情
[http_url] => 链接地址
[goods_id] => 产品id（未关联产品无此字段）
[goods_title] => 产品名称（未关联产品无此字段）
[price] => 售价（未关联产品无此字段）
[zl_price1] => 最小月租（未关联产品无此字段）
[zl_price2] => 最大月租（未关联产品无此字段）
 */
data class Banner(val file_url: String, val contents: String, val http_url: String, val goods_id: Int,
                  val goods_title: String, val price: Double, val zl_price1: Double, val zl_price2: Double)

/**
 * lists
 * 产品列表
 *  [id] => 产品ID
[file_url] => 图片
[title] => 名称
[price] => 售价
[zl_price1] => 最低租金
[zl_price2] => 最高租金
[http_url] => 分享地址
[guige] => 规格
[xinghao] => 型号
[yanse] => 颜色
[num] => 数量

 */

data class Goods(val id: String, val file_url: String, val title: String, val price: Double, val zl_price1: Double, val zl_price2: Double,
                 val http_url: String)

data class StoreDetailsWorker(val info: StoreInfoWorker, val banner: ArrayList<Banner>, val lists: ArrayList<Goods>)

data class StoreDetailsWorkerRes(val retRes: StoreDetailsWorker) : RequestResult()

data class OrderDetailsRes(val retRes: ArrayList<Goods>) : RequestResult()

/**
 * lists
 * 产品列表
[title] => 名称
[file_url] => 图片
[price] => 售价
[zl_price1] => 最少租金
[zl_price2] => 最大租金
[guige] => 规格
[xinghao] => 型号
[yanse] => 颜色
[num] => 数量

 */

data class GoodsNoId(val title: String, val file_url: String, val price: Double, val zl_price1: Double, val zl_price2: Double,
                     val guige: String, val xinghao: String, val yanse: String, val num: Int)


/**
 * 租金列表
 * [qs] => 期数
[price] => 金额
[end_time] => 截至交租日期（时间戳）
[pay_time] => 交租日期（时间戳）
[status] => 状态（1：未交租，2：已交租）
 */
data class ZJList(val qs: String, val price: Double, val end_time: Long, val pay_time: Long, val status: Int)

/**
 * [id] => 订单id
[numbers] => 订单编号
[jx_price] => 绩效发放金额
[jxyff] => 绩效是否已发放（0|1）
[jxff_time] => 绩效发放时间（时间戳）
[jxyqr] => 绩效是否已确认（0|1）
[title] => 客户姓名
[phone] => 电话
[zj_price] => 租金
[yj_price] => 押金
[price] => 支付金额
[yf] => 运费
[goods_price] => 产品总价
[pca] => 湖北省 武汉市 硚口区
[address] => 汉口城市广场1栋1101室
[pj_contents] => 评价内容
[pj_time] => 评价时间（时间戳）
[create_time] => 订单时间（时间戳）
[lists] => Array（产品列表）
[zg_zq] => 总租期
[zuqilist] => Array（租金列表）
 */

data class OrdersDetails(val id: Int, val numbers: String, val jx_price: Double, val jxyff: Int, val jxff_time: Long, val jxyqr: Int, val title: String,
                         val phone: String, val zj_price: Double, val yj_price: Double, val price: Double, val yf: Double, val goods_price: Double,
                         val pca: String, val address: String, val pj_contents: String, val pj_time: Long, val create_time: Long,
                         val lists: ArrayList<GoodsNoId>, val zg_zq: String, val zuqilist: ArrayList<ZJList>)

data class OrdersDetailsRes(val retRes: OrdersDetails) : RequestResult()


/**
 * 会员信息
 *   [id] => 账号id
[account] => 18614221224
[title] => 设计师02
 */
data class VipInfo(val id: Int, val account: String, val title: String)

data class VipListRes(val retRes: ArrayList<VipInfo>) : RequestResult()

/**
 * [type_id] => 类型（1:优惠券，2：押金券）
[account_id] => 账户id
[title] => 60元优惠券
[price] => 60.00
[start_time] => 开始时间（时间戳）
[end_time] => 结束时间（时间戳）
[status] => 状态（1：未使用，2：已使用，3：已过期）
[create_time] => 发放时间（时间戳）
[account_title] => 账户名
[account] => 账号
 */
data class YHQInfo(val type_id: Int, val account_id: Int, val title: String, val price: Double, val start_time: Long,
                   val end_time: Long, val status: Int, val create_time: Long, val account_title: String, val account: String)

data class YHQInfoListRes(val retRes: ArrayList<YHQInfo>) : RequestResult()


/**
 * 员工绩效
 * [id] => 订单ID
[numbers] => 订单编号
[title] => 昵称
[account] => 会员账号
[goods_num] => 商品数量
[price] => 订单成交价
[type] => 组购类型 （zl：租赁，gm：购买)
[create_time] => 订单创建时间（时间戳）
[ztyg_id] => 员工id
[jxyff] => 绩效是否已发放（0|1）
[jx_price] => 已发放绩效金额
[jxyqr] => 员工是否确认
[jxff_time] => 绩效发放时间（时间戳）
 */
data class WorkerGrade(val id: Int, val numbers: String, val title: String, val account: String, val goods_num: Int, val price: Double, val type: String,
                       val create_time: Long, val ztyg_id: Int, val jxyff: Int, var jx_price: Double, var jxyqr: Int, val jxff_time: Long, var backUpPrice: Double = 0.0)

data class WorkerGradeListRes(val retRes: ArrayList<WorkerGrade>) : RequestResult()

/**
 *  [v_title] => 版本名称（1.0）
[v_num] => 版本号（1）
[http_url] => 下载地址
 */
data class VersionInfo(val v_title: String, val v_num: Int, val http_url: String)

data class VersionInfoRes(val retRes: VersionInfo) : RequestResult()