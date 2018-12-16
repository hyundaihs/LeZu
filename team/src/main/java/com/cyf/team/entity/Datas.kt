package com.cyf.team.entity

import com.cyf.lezu.entity.RequestResult

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/1/001.
 */

/*[numbers] => 编号
[num] => 库存数量
[xinjiu] => 新旧程度*/
data class CargoNum(val numbers: String, val num: Int, val xinjiu: String)

/*[id] => 订单详情id
[num] => 数量
[goods_title] => 名称
[guige] => 规格
[yanse] => 颜色
[xinghao] => 型号
[kucunguige_id] => 规格id
[kucunguige_num] => 库存数量
[ck_status] => 是否出库（0|1）
[ck_time] => 出库时间（时间戳）
[rk_status] => 是否入库（0|1）
[rk_time] => 入库时间（时间戳）
[numbers_lists] => Array（出库编号）*/
data class CargoDetails(val id: Int, val num: Int, val goods_title: String, val guige: String, val yanse: String, val xinghao: String, val kucunguige_id: String
                        , val kucunguige_num: Int, val ck_status: Int, val ck_time: Long, val rk_status: Int, val rk_time: Long, val numbers_lists: ArrayList<CargoNum>)

/*
[id] => 订单id
[numbers] => 订单编号
[title] => 收件人
[phone] => 电话
[pca] => 省 市 区
[address] => 详细地址
[wl_title] => 物流名称
[wl_numbers] => 物流编号
[contents] => 客户备注
[create_time] => 下单时间（时间戳）
[ck_status] => 是否出库（0|1）
[ck_time] => 出库时间（时间戳）
[rk_status] => 是否入库（0|1）
[rk_time] => 入库时间（时间戳）
[ps_status] => 配送状态（1:待配送,2：配送中，3：已送达，4：安装完成（已审核））
[ps_contents] => 配送备注
[ps_admin_id] => 配送工人id
[ps_admin_phone] => 配送工人电话
[ps_admin_title] => 配送工人名称
[ps_fp_time] => 配送分配时间戳
[hs_status] => 回收状态（1:待回收，2：回收中，3：已回收,4:已入库）
[hs_contents] => 回收备注
[hs_admin_id] => 回收工人id
[hs_admin_phone] => 回收工人电话
[hs_admin_title] => 回收工人名称
[hs_fp_time] => 回收分配时间戳
[lists] => Array
[type] => 订单类型（gm：购买，zl：租赁）*/
data class CargoOrder(val id: Int, val numbers: String, val title: String, val phone: String, val pca: String, val address: String, val wl_title: String
                      , val wl_numbers: String, val contents: String, val create_time: Long, val ck_status: Int, val ck_time: Long, val rk_status: Int
                      , val rk_time: Long, val ps_status: Int, val ps_contents: String, val ps_admin_id: Int, val ps_admin_phone: String, val ps_admin_title: String
                      , val ps_fp_time: Long, val hs_status: Int, val hs_admin_id: Int, val hs_admin_phone: String, val hs_admin_title: String, val hs_fp_time: Long
                      , val lists: ArrayList<CargoDetails>, val type: String)

data class CargoOrdersRes(val retRes: ArrayList<CargoOrder>) : RequestResult()

/*[id] => 分类id
[file_url] => 图片
[title] => 名称*/
data class CargoType(val id: Int, val file_url: String, val title: String)

data class CargoTypeListRes(val retRes: ArrayList<CargoType>) : RequestResult()

/*[id] => 规格id
[title] => 规格名称
[yanse] => 颜色
[xinghao] => 型号
[num] => 库存数量
[numbers_lists] => Array(库存编号列表)*/
data class CargoDetail(val id: Int, val title: String, val yanse: String, val xinghao: String, val num: Int, val numbers_lists: ArrayList<CargoNum>)

/*[id] => 产品id
[title] => 名称
[file_url] =>
[lists] => Array（规格列表）*/
data class CargoKucun(val id: Int, val title: String, val file_url: String, val lists: ArrayList<CargoDetail>)

data class CargoKucunListRes(val retRes: ArrayList<CargoKucun>) : RequestResult()

/*[id] => 账号id
[type_id] => 类型（1：个人，2：企业）
[is_sjs] => 是否是设计师
[account] => 账号
[title] => 昵称
[name] => 公司/个人名称
[id_numbers] => 公司营业执照号码/个人身份证
[id_file_url1] => 个人/法人身份证正面
[id_file_url2] => 个人/法人身份证反面
[link_man] => 联系人
[link_phone] => 联系人电话
[yyzz_file_url] => 营业执照照片
[email] => 邮箱
[fr_name] => 公司法人姓名
[fr_link_phone] => 公司法人电话
[company_title] => 公司名称
[sex] => 性别
[zl_status] => 资料审核状态（0,：未提交，1：审核中，2：通过，3：未通过）
[zl_time] => 资料提交时间
[zl_contents] => 资料备注*/
data class UserInfo(val id: Int, val type_id: Int, val is_sjs: Int, val account: String, val title: String, val name: String, val id_numbers: String
                    , val id_file_url1: String, val id_file_url2: String, val link_man: String, val link_phone: String, val yyzz_file_url: String, val email: String
                    , val fr_name: String, val fr_link_phone: String, val company_title: String, val sex: String, val zl_status: Int, val zl_time: Long
                    , val zl_contents: String)

data class UserInfoListRes(val retRes: ArrayList<UserInfo>) : RequestResult()

/*[file_url] => 图片地址
[create_time] => 上传时间*/
data class PhotoInfo(val file_url: String, val create_time: Long)

/*[id] => 资料ID（设置分数用）
[account_id] => 账号ID
[score] => 当前得分
[max] => 最高分数
[title] => 资料名称（人民银行个人征信）
[lists] => Array（图片列表）*/
data class CompanyInfo(val id: Int, val account_id: String, val score: String, val max: String, val title: String, val lists: ArrayList<PhotoInfo>)

/*[id] => 资料ID（设置分数用）
[account_id] => 账号ID
[score] => 当前得分
[max] => 最高分数
[title] => 资料名称（人民银行个人征信）
[lists] => Array（图片列表）*/
data class PersonalQual(val id: Int, val account_id: String, val score: String, val max: String, val title: String, val lists: ArrayList<PhotoInfo>)

data class PersonalQualTemp(val id_title: Int, val id: Int, val account_id: String, val score: String, val max: String, val title: String, val file_url: String, val create_time: Long)

/*[id] => 账号ID
[title] => 昵称
[account] => 账号
[type_id] => 类型（1：个人，2：企业）
[zl_time] => 资料上传时间
[grLists] => Array（个人资质列表）
[gsLists] => Array（公司资料列表）*/
data class CreditInfo(val id: Int, val title: String, val account: String, val type_id: Int, val zl_time: Long, val grLists: ArrayList<PersonalQual>
                      , val gsLists: ArrayList<PersonalQual>)

data class CreditInfoListRes(val retRes: ArrayList<CreditInfo>) : RequestResult()