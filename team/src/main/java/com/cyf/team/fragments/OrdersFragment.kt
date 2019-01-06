package com.cyf.team.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cyf.lezu.D
import com.cyf.lezu.E
import com.cyf.lezu.adapters.LineDecoration
import com.cyf.lezu.adapters.MyBaseAdapter
import com.cyf.lezu.entity.LoginInfoRes
import com.cyf.lezu.fragments.BaseFragment
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.CalendarUtil
import com.cyf.lezu.utils.CustomDialog
import com.cyf.lezu.utils.LoginErrDialog
import com.cyf.lezu.utils.ToastUtil
import com.cyf.lezu.widget.SwipeRefreshAndLoadLayout
import com.cyf.team.AppTeam
import com.cyf.team.OrderListType.Companion.GR_ORDER_IN
import com.cyf.team.OrderListType.Companion.GR_ORDER_OUT
import com.cyf.team.OrderListType.Companion.KG_MISSION_IN
import com.cyf.team.OrderListType.Companion.KG_MISSION_OUT
import com.cyf.team.OrderListType.Companion.KG_ORDER_ALL
import com.cyf.team.OrderListType.Companion.KG_ORDER_CANCEL
import com.cyf.team.OrderListType.Companion.KG_ORDER_CONFIRMED
import com.cyf.team.OrderListType.Companion.KG_ORDER_IN
import com.cyf.team.OrderListType.Companion.KG_ORDER_SEND
import com.cyf.team.OrderListType.Companion.KG_ORDER_WAIT_CONFIRM
import com.cyf.team.OrderListType.Companion.XJ_ORDER_IN
import com.cyf.team.OrderListType.Companion.XJ_ORDER_OUT
import com.cyf.team.R
import com.cyf.team.activities.AddKucunActivity
import com.cyf.team.activities.LocationActivity
import com.cyf.team.activities.LoginActivity
import com.cyf.team.activities.WorkerListActivity
import com.cyf.team.entity.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_orders.*
import kotlinx.android.synthetic.main.layout_orders_item.view.*
import kotlinx.android.synthetic.main.layout_orders_item_cargos.view.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/1/001.
 */
class OrdersFragment : BaseFragment() {

    val cargoOrder = ArrayList<CargoOrder>()
    private val mAdapter = MyAdapter(cargoOrder)
    private var status = 0
    private var pageType = KG_MISSION_OUT
    private var inter: String = ""
    private var isLoadMore = false

    companion object {
        val HS_STATUS = listOf("", "待回收", "回收中", "已回收", "已入库")
        val PS_STATUS = listOf("", "待配送", "配送中", "已送达", "安装完成（已审核）")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments != null) {
            pageType = arguments!!.getInt("pageType")
            when (pageType) {
                KG_MISSION_OUT -> {
                    status = 3
                    inter = "orderslists"
                    isLoadMore = false
                }
                KG_MISSION_IN -> {
                    status = 8
                    inter = "orderslists"
                    isLoadMore = false
                }
                KG_ORDER_ALL -> {
                    status = 0
                    inter = "cgd"
                    isLoadMore = true
                }
                KG_ORDER_WAIT_CONFIRM -> {
                    status = 1
                    inter = "cgd"
                    isLoadMore = true
                }
                KG_ORDER_CONFIRMED -> {
                    status = 2
                    inter = "cgd"
                    isLoadMore = true
                }
                KG_ORDER_SEND -> {
                    status = 3
                    inter = "cgd"
                    isLoadMore = true
                }
                KG_ORDER_CANCEL -> {
                    status = 4
                    inter = "cgd"
                    isLoadMore = true
                }
                KG_ORDER_IN -> {
                    status = 5
                    inter = "cgd"
                    isLoadMore = true
                }
                XJ_ORDER_OUT -> {
                    status = 3
                    inter = "orderslistsxj"
                    isLoadMore = false
                }
                XJ_ORDER_IN -> {
                    status = 8
                    inter = "orderslistsxj"
                    isLoadMore = false
                }
                GR_ORDER_OUT -> {
                    status = 3
                    inter = "orderslistsgr"
                    isLoadMore = false
                }
                GR_ORDER_IN -> {
                    status = 8
                    inter = "orderslistsgr"
                    isLoadMore = false
                }
            }
            initViews()
        } else {
            activity?.toast("arguments is null")
        }
    }

    override fun onResume() {
        super.onResume()
        getCargoOrders(ordersSwipe.currPage, true)
    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(context)
        ordersList.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        ordersList.itemAnimator = DefaultItemAnimator()
        ordersList.isNestedScrollingEnabled = false
        ordersSwipe.setOnRefreshListener(object : SwipeRefreshAndLoadLayout.OnRefreshListener {
            override fun onRefresh() {
                getCargoOrders(ordersSwipe.currPage, true)
            }

            override fun onLoadMore(currPage: Int, totalPages: Int) {
                if (isLoadMore) {
                    getCargoOrders(currPage, false)
                } else {
                    ordersSwipe.isRefreshing = false
                }
            }
        })
        ordersList.adapter = mAdapter
        mAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
//                val intent = Intent(view.context, FishLogDetailsActivity::class.java)
//                intent.putExtra("id", cargoOrder[position].id)
//                startActivity(intent)
            }
        }

    }

    private inner class MyAdapter(val data: ArrayList<CargoOrder>) : MyBaseAdapter(R.layout.layout_orders_item) {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val cargoOrder = data[position]
            holder.itemView.orderId.text = "订单号：${cargoOrder.numbers}"
            holder.itemView.orderTime.text = "下单时间：${CalendarUtil(cargoOrder.create_time, true).format(CalendarUtil.STANDARD)}"
            holder.itemView.chTime.text = "更新时间：${CalendarUtil(cargoOrder.update_time, true).format(CalendarUtil.STANDARD)}"
            val adapter = MyCargoAdapter(cargoOrder.lists)
            when (pageType) {
                KG_MISSION_OUT -> {
                    holder.itemView.layoutWorker.visibility = View.GONE
                    var isCG = false
                    for (i in 0 until cargoOrder.lists.size) {
                        val cargoDetails = cargoOrder.lists[i]
                        if (cargoDetails.num > cargoDetails.kucunguige_num) {
                            isCG = true
                            break
                        }
                    }
                    holder.itemView.orderStatus.text = PS_STATUS[cargoOrder.ps_status]

                    if (isCG && cargoOrder.ps_status == 1) {
                        holder.itemView.sendWork.text = "需采购"
                        holder.itemView.sendWork.visibility = View.VISIBLE
                        holder.itemView.sendWork.setOnClickListener {
                            ToastUtil.show(holder.itemView.context, "请到登录管理端->库存管理->库存产品添加产品跟规格，然后采购下单", Toast.LENGTH_LONG)
                        }
                    } else {
                        if (cargoOrder.ck_status == 0 && cargoOrder.ps_status == 2) {
                            holder.itemView.sendWork.text = "发货"
                            holder.itemView.sendWork.visibility = View.VISIBLE
                            holder.itemView.sendWork.setOnClickListener {
                                //发货
                                activity?.CustomDialog("提示", "确定要发货吗？", positiveClicked = DialogInterface.OnClickListener { dialog, which ->
                                    sendGoods(cargoOrder.type, cargoOrder.id, -1)//1：入库，-1：出库
                                }, negative = "取消")
                            }
                        } else {
                            holder.itemView.sendWork.visibility = View.GONE
                        }
                    }
                    adapter.myOnItemClickListener = object : MyOnItemClickListener {
                        override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                            if (cargoOrder.lists[position].ck_status == 0) {
                                //添加库存
                                AppTeam.cargoDetails = cargoOrder.lists[position]
                                val intent = Intent(context, AddKucunActivity::class.java)
                                intent.putExtra("isCK", true)
                                intent.putExtra("type", cargoOrder.type)
                                intent.putExtra("id", cargoOrder.id)
                                startActivity(intent)
                            } else {
                                context?.toast("货物已出库，无需添加")
                            }
                        }
                    }
                }
                KG_MISSION_IN -> {
                    holder.itemView.layoutWorker.visibility = View.GONE
                    holder.itemView.orderStatus.text = HS_STATUS[cargoOrder.hs_status]
                    adapter.myOnItemClickListener = object : MyOnItemClickListener {
                        override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                            if (cargoOrder.lists[position].rk_status == 1) {
                                context?.toast("货物已入库，无需添加")
                            } else {
                                if (cargoOrder.lists[position].numbers_lists.size > 0) {
                                    //进入添加入库
                                    AppTeam.cargoDetails = cargoOrder.lists[position]
                                    val intent = Intent(context, AddKucunActivity::class.java)
                                    intent.putExtra("isCK", false)
                                    intent.putExtra("type", cargoOrder.type)
                                    intent.putExtra("id", cargoOrder.id)
                                    startActivity(intent)
                                } else {
                                    context?.toast("此商品初始化未入库，存在异常！")
                                }
                            }
                        }
                    }
                    if (cargoOrder.hs_status == 3) {
                        holder.itemView.sendWork.text = "确定入库"
                        holder.itemView.sendWork.visibility = View.VISIBLE
                        holder.itemView.sendWork.setOnClickListener {
                            //确认入库
                            var isRk = true
                            for (i in 0 until cargoOrder.lists.size) {
                                if (cargoOrder.lists[i].rk_status == 0) {
                                    isRk = false
                                }
                            }
                            if (isRk) {
                                //入库
                                activity?.CustomDialog("提示", "确定要入库吗？", positiveClicked = DialogInterface.OnClickListener { dialog, which ->
                                    sendGoods(cargoOrder.type, cargoOrder.id, 1)//1：入库，-1：出库
                                }, negative = "取消")
                            } else {
                                context?.toast("商品未全部入库")
                            }
                        }
                    } else {
                        holder.itemView.sendWork.visibility = View.GONE
                    }

                }
                KG_ORDER_ALL -> {
                    holder.itemView.orderStatus.visibility = View.GONE
                    holder.itemView.chTime.text = "备注：${cargoOrder.contents}"
                    holder.itemView.layoutWorker.visibility = View.GONE
                }
                KG_ORDER_WAIT_CONFIRM -> {
                    holder.itemView.orderStatus.visibility = View.GONE
                    holder.itemView.chTime.text = "备注：${cargoOrder.contents}"
                    holder.itemView.layoutWorker.visibility = View.GONE
                }
                KG_ORDER_CONFIRMED -> {
                    holder.itemView.orderStatus.visibility = View.GONE
                    holder.itemView.chTime.text = "备注：${cargoOrder.contents}"
                    holder.itemView.layoutWorker.visibility = View.GONE
                }
                KG_ORDER_SEND -> {
                    holder.itemView.orderStatus.visibility = View.GONE
                    holder.itemView.chTime.text = "备注：${cargoOrder.contents}"
                    holder.itemView.layoutWorker.visibility = View.GONE
                }
                KG_ORDER_CANCEL -> {
                    holder.itemView.orderStatus.visibility = View.GONE
                    holder.itemView.chTime.text = "备注：${cargoOrder.contents}"
                    holder.itemView.layoutWorker.visibility = View.GONE
                }
                KG_ORDER_IN -> {
                    holder.itemView.orderStatus.visibility = View.GONE
                    holder.itemView.chTime.text = "备注：${cargoOrder.contents}"
                    holder.itemView.layoutWorker.visibility = View.GONE
                }
                XJ_ORDER_OUT -> {
                    holder.itemView.layoutWorker.visibility = View.VISIBLE
                    holder.itemView.orderStatus.text = PS_STATUS[cargoOrder.ps_status]
                    holder.itemView.layoutAddr.visibility = View.VISIBLE
                    holder.itemView.receiverName.text = cargoOrder.title
                    holder.itemView.receiverPhone.text = cargoOrder.phone
                    holder.itemView.receiverAddr.text = cargoOrder.address
                    holder.itemView.receiverContent.text = cargoOrder.contents
                    when (cargoOrder.ps_status) {
                        1 -> {
                            holder.itemView.hintText.text = "安排工人，并接单后才能看到工人信息"
                            holder.itemView.hintText.visibility = View.VISIBLE
                            holder.itemView.layoutWorkerInfo.visibility = View.GONE
                            holder.itemView.sendWork.text = "安排工人"
                            holder.itemView.sendWork.visibility = View.VISIBLE
                            holder.itemView.sendWork.setOnClickListener {
                                //安排工人    工人巡检列表
                                val intent = Intent(activity, WorkerListActivity::class.java)
                                intent.putExtra("isWorker", true)
                                intent.putExtra("orderId", cargoOrder.id)
                                intent.putExtra("orderType", cargoOrder.type)
                                intent.putExtra("typeId", 1)//1配送，2回收
                                startActivity(intent)
                            }
                        }
                        2 -> {
                            if (cargoOrder.ck_status == 1) {
                                holder.itemView.sendWork.text = "已送达"
                                holder.itemView.sendWork.visibility = View.VISIBLE
                                holder.itemView.sendWork.setOnClickListener {
                                    //送达
                                    activity?.CustomDialog("提示", "确定已送达吗？", positiveClicked = DialogInterface.OnClickListener { dialog, which ->
                                        setSendStatus(cargoOrder.type, cargoOrder.id, 1, 3)//1:待配送,2：配送中，3：已送达，4：安装完成（已审核）
                                    }, negative = "取消")
                                }
                                holder.itemView.hintText.visibility = View.GONE
                                holder.itemView.layoutWorkerInfo.visibility = View.VISIBLE

                            }
                        }
                        3 -> {
                            holder.itemView.sendWork.text = "安装"
                            holder.itemView.sendWork.visibility = View.VISIBLE
                            holder.itemView.sendWork.setOnClickListener {
                                //安装
                                activity?.CustomDialog("提示", "确定已安装吗？", positiveClicked = DialogInterface.OnClickListener { dialog, which ->
                                    setSendStatus(cargoOrder.type, cargoOrder.id, 1, 4)//1:待配送,2：配送中，3：已送达，4：安装完成（已审核）
                                }, negative = "取消")
                            }
                            holder.itemView.hintText.visibility = View.GONE
                            holder.itemView.layoutWorkerInfo.visibility = View.VISIBLE
                        }
                        else -> {
                            holder.itemView.sendWork.visibility = View.GONE
                            holder.itemView.hintText.visibility = View.GONE
                            holder.itemView.layoutWorkerInfo.visibility = View.VISIBLE
                        }
                    }
                    holder.itemView.psNameTitle.text = "工人："
                    holder.itemView.psName.text = cargoOrder.ps_admin_title
                    holder.itemView.psPhone.text = cargoOrder.ps_admin_phone
                    holder.itemView.checkLocal.setOnClickListener {
                        //查看位置
                        getLocal(cargoOrder.ps_admin_id)
                    }

                }
                XJ_ORDER_IN -> {
                    holder.itemView.layoutWorker.visibility = View.VISIBLE
                    holder.itemView.orderStatus.text = HS_STATUS[cargoOrder.hs_status]
                    holder.itemView.layoutAddr.visibility = View.VISIBLE
                    holder.itemView.receiverName.text = cargoOrder.title
                    holder.itemView.receiverPhone.text = cargoOrder.phone
                    holder.itemView.receiverAddr.text = cargoOrder.address
                    holder.itemView.receiverContent.text = cargoOrder.contents
                    when (cargoOrder.hs_status) {
                        1 -> {
                            holder.itemView.sendWork.visibility = View.VISIBLE
                            holder.itemView.sendWork.text = "安排工人"
                            holder.itemView.sendWork.setOnClickListener {
                                //安排工人    工人巡检列表
                                val intent = Intent(activity, WorkerListActivity::class.java)
                                intent.putExtra("isWorker", true)
                                intent.putExtra("orderId", cargoOrder.id)
                                intent.putExtra("orderType", cargoOrder.type)
                                intent.putExtra("typeId", 2)//1配送，2回收
                                startActivity(intent)
                            }
                        }
                        2 -> {
                            holder.itemView.sendWork.visibility = View.VISIBLE
                            holder.itemView.sendWork.text = "已回收"
                            holder.itemView.sendWork.setOnClickListener {
                                //回收
                                activity?.CustomDialog("提示", "确定已回收吗？", positiveClicked = DialogInterface.OnClickListener { dialog, which ->
                                    setSendStatus(cargoOrder.type, cargoOrder.id, 2, 3)//1:待配送,2：配送中，3：已送达，4：安装完成（已审核）
                                }, negative = "取消")
                            }
                        }
                        else -> {
                            holder.itemView.sendWork.visibility = View.GONE
                        }
                    }

                }
                GR_ORDER_OUT -> {
                    holder.itemView.layoutWorker.visibility = View.VISIBLE
                    holder.itemView.orderStatus.text = PS_STATUS[cargoOrder.ps_status]
                    holder.itemView.layoutAddr.visibility = View.VISIBLE
                    holder.itemView.receiverName.text = cargoOrder.title
                    holder.itemView.receiverPhone.text = cargoOrder.phone
                    holder.itemView.receiverAddr.text = cargoOrder.address
                    holder.itemView.receiverContent.text = cargoOrder.contents
                    when (cargoOrder.ps_status) {
                        1 -> {
                            holder.itemView.hintText.text = "接单后才能看到巡检信息"
                            holder.itemView.hintText.visibility = View.VISIBLE
                            holder.itemView.layoutWorkerInfo.visibility = View.GONE
                            holder.itemView.sendWork.text = "抢单"
                            holder.itemView.sendWork.visibility = View.VISIBLE
                            holder.itemView.sendWork.setOnClickListener {
                                //抢单配送
                                activity?.CustomDialog("提示", "确定要接收订单吗？", positiveClicked = DialogInterface.OnClickListener { dialog, which ->
                                    getOrder(cargoOrder.type, cargoOrder.id, 1)
                                }, negative = "取消")
                            }
                        }
                        2 -> {
                            holder.itemView.hintText.visibility = View.GONE
                            holder.itemView.layoutWorkerInfo.visibility = View.VISIBLE

                        }
                        3 -> {
                            holder.itemView.sendWork.text = "安装"
                            holder.itemView.sendWork.visibility = View.VISIBLE
                            holder.itemView.sendWork.setOnClickListener {
                                //安装
                                activity?.CustomDialog("提示", "确定已安装吗？", positiveClicked = DialogInterface.OnClickListener { dialog, which ->
                                    setSendStatus(cargoOrder.type, cargoOrder.id, 1, 4)//1:待配送,2：配送中，3：已送达，4：安装完成（已审核）
                                }, negative = "取消")
                            }
                            holder.itemView.hintText.visibility = View.GONE
                            holder.itemView.layoutWorkerInfo.visibility = View.VISIBLE
                        }
                        else -> {
                            holder.itemView.sendWork.visibility = View.GONE
                            holder.itemView.hintText.visibility = View.GONE
                            holder.itemView.layoutWorkerInfo.visibility = View.VISIBLE
                        }
                    }
                    holder.itemView.psNameTitle.text = "巡检："
                    holder.itemView.psName.text = cargoOrder.xj_admin_title
                    holder.itemView.psPhone.text = cargoOrder.xj_admin_phone
                    holder.itemView.checkLocal.setOnClickListener {
                        //查看位置
                        getLocal(cargoOrder.xj_admin_id)
                    }
                }
                GR_ORDER_IN -> {
                    holder.itemView.layoutWorker.visibility = View.VISIBLE
                    holder.itemView.orderStatus.text = HS_STATUS[cargoOrder.hs_status]
                    holder.itemView.layoutAddr.visibility = View.VISIBLE
                    holder.itemView.receiverName.text = cargoOrder.title
                    holder.itemView.receiverPhone.text = cargoOrder.phone
                    holder.itemView.receiverAddr.text = cargoOrder.address
                    holder.itemView.receiverContent.text = cargoOrder.contents
                    when (cargoOrder.hs_status) {
                        1 -> {
                            holder.itemView.sendWork.visibility = View.VISIBLE
                            holder.itemView.sendWork.text = "抢单"
                            holder.itemView.sendWork.setOnClickListener {
                                //抢单回收
                                activity?.CustomDialog("提示", "确定要接收订单吗？", positiveClicked = DialogInterface.OnClickListener { dialog, which ->
                                    getOrder(cargoOrder.type, cargoOrder.id, 2)
                                }, negative = "取消")
                            }
                        }
                        else -> {
                            holder.itemView.sendWork.visibility = View.GONE
                        }
                    }
                }
            }

            val layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.itemView.orderCargos.layoutManager = layoutManager
            layoutManager.orientation = OrientationHelper.VERTICAL
            holder.itemView.orderCargos.addItemDecoration(LineDecoration(holder.itemView.context, LineDecoration.VERTICAL))
            holder.itemView.orderCargos.itemAnimator = DefaultItemAnimator()
            holder.itemView.orderCargos.isNestedScrollingEnabled = false
            holder.itemView.orderCargos.adapter = adapter

        }

        override fun getItemCount(): Int = data.size
    }

    private inner class MyCargoAdapter(val data: ArrayList<CargoDetails>) : MyBaseAdapter(R.layout.layout_orders_item_cargos) {
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val cargoDetails = data[position]
            holder.itemView.cargoName.text = if (cargoDetails.goods_title == null || cargoDetails.goods_title.isEmpty()) cargoDetails.title else cargoDetails.goods_title
            holder.itemView.cargoColor.text = cargoDetails.yanse
            holder.itemView.cargoType.text = cargoDetails.guige
            holder.itemView.cargoModel.text = cargoDetails.xinghao
            when (pageType) {
                KG_MISSION_OUT -> {
                    if (cargoDetails.ck_status == 0) {
                        holder.itemView.cargoStatus.text = "未出库"
                    } else {
                        holder.itemView.cargoStatus.text = "货物编号：${cargoDetails.numbers_lists[0].numbers}"
                    }
                    holder.itemView.isKC.visibility = if (cargoDetails.kucunguige_num < cargoDetails.num) View.VISIBLE else View.GONE
                    holder.itemView.cargoNum.text = cargoDetails.num.toString()
                    holder.itemView.kcNum.text = cargoDetails.kucunguige_num.toString()
                }
                KG_MISSION_IN -> {
                    if (cargoDetails.rk_status == 0) {
                        holder.itemView.cargoStatus.text = "未入库"
                    } else {
                        holder.itemView.cargoStatus.text = "货物编号：${cargoDetails.numbers_lists[0].numbers}"
                    }
                    holder.itemView.isKC.visibility = if (cargoDetails.kucunguige_num < cargoDetails.num) View.VISIBLE else View.GONE
                    holder.itemView.cargoNum.text = cargoDetails.num.toString()
                    holder.itemView.kcNum.text = cargoDetails.kucunguige_num.toString()
                }
                KG_ORDER_ALL -> {
                    holder.itemView.cargoStatus.visibility = View.GONE
                    holder.itemView.isKC.visibility = View.GONE
                    holder.itemView.cargoNum.text = cargoDetails.num.toString()
                    holder.itemView.kcNum.text = cargoDetails.num.toString()
                }
                KG_ORDER_WAIT_CONFIRM -> {
                    holder.itemView.cargoStatus.visibility = View.GONE
                    holder.itemView.isKC.visibility = View.GONE
                    holder.itemView.cargoNum.text = cargoDetails.num.toString()
                    holder.itemView.kcNum.text = cargoDetails.num.toString()
                }
                KG_ORDER_CONFIRMED -> {
                    holder.itemView.cargoStatus.visibility = View.GONE
                    holder.itemView.isKC.visibility = View.GONE
                    holder.itemView.cargoNum.text = cargoDetails.num.toString()
                    holder.itemView.kcNum.text = cargoDetails.num.toString()
                }
                KG_ORDER_SEND -> {
                    holder.itemView.cargoStatus.visibility = View.GONE
                    holder.itemView.isKC.visibility = View.GONE
                    holder.itemView.cargoNum.text = cargoDetails.num.toString()
                    holder.itemView.kcNum.text = cargoDetails.num.toString()
                }
                KG_ORDER_CANCEL -> {
                    holder.itemView.cargoStatus.visibility = View.GONE
                    holder.itemView.isKC.visibility = View.GONE
                    holder.itemView.cargoNum.text = cargoDetails.num.toString()
                    holder.itemView.kcNum.text = cargoDetails.num.toString()
                }
                KG_ORDER_IN -> {
                    holder.itemView.cargoStatus.visibility = View.GONE
                    holder.itemView.isKC.visibility = View.GONE
                    holder.itemView.cargoNum.text = cargoDetails.num.toString()
                    holder.itemView.kcNum.text = cargoDetails.num.toString()
                }
                XJ_ORDER_OUT -> {
                    if (cargoDetails.ck_status == 0) {
                        holder.itemView.cargoStatus.text = "未出库"
                    } else {
                        holder.itemView.cargoStatus.text = "已出库\n货物编号：${cargoDetails.numbers_lists[0].numbers}"
                    }
                    holder.itemView.layoutKC.visibility = View.GONE
                    holder.itemView.cargoNum.text = cargoDetails.num.toString()
                    holder.itemView.kcNum.text = cargoDetails.kucunguige_num.toString()
                }
                XJ_ORDER_IN -> {
                    if (cargoDetails.rk_status == 0) {
                        holder.itemView.cargoStatus.text = "未入库"
                    } else {
                        holder.itemView.cargoStatus.text = "已入库\n货物编号：${cargoDetails.numbers_lists[0].numbers}"
                    }
                    holder.itemView.layoutKC.visibility = View.GONE
                    holder.itemView.cargoNum.text = cargoDetails.num.toString()
                    holder.itemView.kcNum.text = cargoDetails.kucunguige_num.toString()
                }
                GR_ORDER_OUT -> {
                    if (cargoDetails.ck_status == 0) {
                        holder.itemView.cargoStatus.text = "未出库"
                    } else {
                        holder.itemView.cargoStatus.text = "已出库\n货物编号：${cargoDetails.numbers_lists[0].numbers}"
                    }
                    holder.itemView.layoutKC.visibility = View.GONE
                    holder.itemView.cargoNum.text = cargoDetails.num.toString()
                    holder.itemView.kcNum.text = cargoDetails.kucunguige_num.toString()
                }
                GR_ORDER_IN -> {
                    if (cargoDetails.rk_status == 0) {
                        holder.itemView.cargoStatus.text = "未入库"
                    } else {
                        holder.itemView.cargoStatus.text = "已入库\n货物编号：${cargoDetails.numbers_lists[0].numbers}"
                    }
                    holder.itemView.layoutKC.visibility = View.GONE
                    holder.itemView.cargoNum.text = cargoDetails.num.toString()
                    holder.itemView.kcNum.text = cargoDetails.kucunguige_num.toString()
                }
            }
        }

        override fun getItemCount(): Int = data.size
    }

    private fun getCargoOrders(page: Int, isRefresh: Boolean) {
        ordersSwipe?.isRefreshing = true
        val map = mapOf(
                Pair("status", status.toString())
                , Pair("page", page.toString())
                , Pair("ps_status", "0")
                , Pair("hs_status", "0")
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val cargoOrdersRes = Gson().fromJson(result, CargoOrdersRes::class.java)
                if (isLoadMore) {
                    ordersSwipe?.totalPages = if (cargoOrdersRes.retCounts % 20 == 0)
                        cargoOrdersRes.retCounts / 20 else cargoOrdersRes.retCounts / 20 + 1
                }
                if (isRefresh) {
                    cargoOrder.clear()
                }
                cargoOrder.addAll(cargoOrdersRes.retRes)
                mAdapter.notifyDataSetChanged()
                ordersSwipe?.isRefreshing = false
            }

            override fun onError(context: Context, error: String) {
                ordersSwipe?.isRefreshing = false
            }

            override fun onLoginErr(context: Context) {
                ordersSwipe?.isRefreshing = false
            }

        }, false).postRequest(activity as Context, inter.getInterface(), map)
    }

    //抢单
    private fun getOrder(orders_type: String, orders_id: Int, type_id: Int) {
        val map = mapOf(
                Pair("orders_type", orders_type)
                , Pair("orders_id", orders_id.toString())
                , Pair("type_id", type_id.toString())
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                activity?.toast("操作成功")
                getCargoOrders(ordersSwipe.currPage, true)
            }

            override fun onError(context: Context, error: String) {
                activity?.toast(error)
            }

            override fun onLoginErr(context: Context) {
                activity?.LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, false).postRequest(activity as Context, "qrjd".getInterface(), map)
    }

    //发货
    private fun sendGoods(orders_type: String, orders_id: Int, num: Int) {
        val map = mapOf(
                Pair("orders_type", orders_type)
                , Pair("orders_id", orders_id.toString())
                , Pair("num", num.toString())
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                activity?.toast("操作成功")
                getCargoOrders(ordersSwipe.currPage, true)
            }

            override fun onError(context: Context, error: String) {
                activity?.toast(error)
            }

            override fun onLoginErr(context: Context) {
                activity?.LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, false).postRequest(activity as Context, "orderckrk".getInterface(), map)
    }

    //设置配送和回收状态
    private fun setSendStatus(orders_type: String, orders_id: Int, type_id: Int, status: Int) {
        val map = mapOf(
                Pair("orders_type", orders_type)
                , Pair("orders_id", orders_id.toString())
                , Pair("type_id", type_id.toString())
                , Pair("status", status.toString())
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                activity?.toast("操作成功")
                getCargoOrders(ordersSwipe.currPage, true)
            }

            override fun onError(context: Context, error: String) {
                activity?.toast(error)
            }

            override fun onLoginErr(context: Context) {
                activity?.LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, false).postRequest(activity as Context, "setpsstats".getInterface(), map)
    }

    //设置配送和回收状态
    private fun getLocal(id: Int) {
        val map = mapOf(
                Pair("admin_id", id.toString())
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val workerRes = Gson().fromJson(result, WorkerRes::class.java)
                val intent = Intent(context, LocationActivity::class.java)
                intent.putExtra("lat", workerRes.retRes.lat)
                intent.putExtra("lng", workerRes.retRes.lng)
                startActivity(intent)
            }

            override fun onError(context: Context, error: String) {
                activity?.toast(error)
            }

            override fun onLoginErr(context: Context) {
                activity?.LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, false).postRequest(activity as Context, "grjwd".getInterface(), map)
    }
}