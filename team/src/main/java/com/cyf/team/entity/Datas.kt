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

