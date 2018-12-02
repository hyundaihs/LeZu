package com.cyf.team.fragments

import android.content.Context
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
                GR_ORDER_OUT->{
                    status = 3
                    inter = "orderslistsgr"
                    isLoadMore = false
                }
                GR_ORDER_IN->{
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

    override fun onStart() {
        super.onStart()
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
            holder.itemView.chTime.text = "出货时间：${cargoOrder.create_time}"
            when (pageType) {
                KG_MISSION_OUT -> {
                    var isCG = false
                    for (i in 0 until cargoOrder.lists.size) {
                        val cargoDetails = cargoOrder.lists[i]
                        if (cargoDetails.num > cargoDetails.kucunguige_num) {
                            isCG = true
                            break
                        }
                    }
                    holder.itemView.orderStatus.text = "需采购"
                    holder.itemView.orderStatus.visibility = if (isCG) View.VISIBLE else View.GONE
                    holder.itemView.orderStatus.setOnClickListener {
                        ToastUtil.show(holder.itemView.context, "请到登录管理端->库存管理->库存产品添加产品跟规格，然后采购下单", Toast.LENGTH_LONG)
                    }
                }
                KG_MISSION_IN -> {
                    holder.itemView.orderStatus.text = HS_STATUS[cargoOrder.hs_status]
                }
                KG_ORDER_ALL -> {
                    holder.itemView.orderStatus.visibility = View.GONE
                }
                KG_ORDER_WAIT_CONFIRM -> {
                    holder.itemView.orderStatus.visibility = View.GONE
                }
                KG_ORDER_CONFIRMED -> {
                    holder.itemView.orderStatus.visibility = View.GONE
                }
                KG_ORDER_SEND -> {
                    holder.itemView.orderStatus.visibility = View.GONE
                }
                KG_ORDER_CANCEL -> {
                    holder.itemView.orderStatus.visibility = View.GONE
                }
                KG_ORDER_IN -> {
                    holder.itemView.orderStatus.visibility = View.GONE
                }
                XJ_ORDER_OUT -> {
                    holder.itemView.orderStatus.text = PS_STATUS[cargoOrder.ps_status]
                    holder.itemView.layoutAddr.visibility = View.VISIBLE
                    holder.itemView.receiverName.text = cargoOrder.title
                    holder.itemView.receiverPhone.text = cargoOrder.phone
                    holder.itemView.receiverAddr.text = cargoOrder.address
                    holder.itemView.receiverContent.text = cargoOrder.contents
                }
                XJ_ORDER_IN -> {
                    holder.itemView.orderStatus.text = PS_STATUS[cargoOrder.ps_status]
                    holder.itemView.layoutAddr.visibility = View.VISIBLE
                    holder.itemView.receiverName.text = cargoOrder.title
                    holder.itemView.receiverPhone.text = cargoOrder.phone
                    holder.itemView.receiverAddr.text = cargoOrder.address
                    holder.itemView.receiverContent.text = cargoOrder.contents
                }
                GR_ORDER_OUT->{
                    holder.itemView.orderStatus.text = PS_STATUS[cargoOrder.ps_status]
                    holder.itemView.layoutAddr.visibility = View.VISIBLE
                    holder.itemView.receiverName.text = cargoOrder.title
                    holder.itemView.receiverPhone.text = cargoOrder.phone
                    holder.itemView.receiverAddr.text = cargoOrder.address
                    holder.itemView.receiverContent.text = cargoOrder.contents
                }
                GR_ORDER_IN->{
                    holder.itemView.orderStatus.text = PS_STATUS[cargoOrder.ps_status]
                    holder.itemView.layoutAddr.visibility = View.VISIBLE
                    holder.itemView.receiverName.text = cargoOrder.title
                    holder.itemView.receiverPhone.text = cargoOrder.phone
                    holder.itemView.receiverAddr.text = cargoOrder.address
                    holder.itemView.receiverContent.text = cargoOrder.contents
                }
            }

            val layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.itemView.orderCargos.layoutManager = layoutManager
            layoutManager.orientation = OrientationHelper.VERTICAL
            holder.itemView.orderCargos.addItemDecoration(LineDecoration(holder.itemView.context, LineDecoration.VERTICAL))
            holder.itemView.orderCargos.itemAnimator = DefaultItemAnimator()
            holder.itemView.orderCargos.isNestedScrollingEnabled = false
            holder.itemView.orderCargos.adapter = MyCargoAdapter(cargoOrder.lists)

        }

        override fun getItemCount(): Int = data.size
    }

    private inner class MyCargoAdapter(val data: ArrayList<CargoDetails>) : MyBaseAdapter(R.layout.layout_orders_item_cargos) {
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val cargoDetails = data[position]
            holder.itemView.cargoName.text = cargoDetails.goods_title
            holder.itemView.cargoColor.text = cargoDetails.yanse
            holder.itemView.cargoType.text = cargoDetails.guige
            holder.itemView.cargoModel.text = cargoDetails.xinghao
            holder.itemView.cargoNum.text = cargoDetails.num.toString()
            holder.itemView.kcNum.text = cargoDetails.kucunguige_num.toString()
            holder.itemView.isKC.visibility = if (cargoDetails.kucunguige_num >= cargoDetails.num) View.VISIBLE else View.GONE
            when (pageType) {
                KG_MISSION_OUT -> {
                    if (cargoDetails.ck_status == 0) {
                        holder.itemView.cargoStatus.text = "未出库"
                    } else {
                        holder.itemView.cargoStatus.text = "货物编号：${cargoDetails.numbers_lists[0].numbers}"
                    }
                }
                KG_MISSION_IN -> {
                    if (cargoDetails.rk_status == 0) {
                        holder.itemView.cargoStatus.text = "未入库"
                    } else {
                        holder.itemView.cargoStatus.text = "货物编号：${cargoDetails.numbers_lists[0].numbers}"
                    }
                }
                KG_ORDER_ALL -> {
                    holder.itemView.cargoStatus.visibility = View.GONE
                }
                KG_ORDER_WAIT_CONFIRM -> {
                    holder.itemView.cargoStatus.visibility = View.GONE
                }
                KG_ORDER_CONFIRMED -> {
                    holder.itemView.cargoStatus.visibility = View.GONE
                }
                KG_ORDER_SEND -> {
                    holder.itemView.cargoStatus.visibility = View.GONE
                }
                KG_ORDER_CANCEL -> {
                    holder.itemView.cargoStatus.visibility = View.GONE
                }
                KG_ORDER_IN -> {
                    holder.itemView.cargoStatus.visibility = View.GONE
                }
                XJ_ORDER_OUT -> {
                    if (cargoDetails.ck_status == 0) {
                        holder.itemView.cargoStatus.text = "未出库"
                    } else {
                        holder.itemView.cargoStatus.text = "已出库\n货物编号：${cargoDetails.numbers_lists[0].numbers}"
                    }
                    holder.itemView.layoutKC.visibility = View.GONE
                }
                XJ_ORDER_IN -> {
                    if (cargoDetails.rk_status == 0) {
                        holder.itemView.cargoStatus.text = "未入库"
                    } else {
                        holder.itemView.cargoStatus.text = "已入库\n货物编号：${cargoDetails.numbers_lists[0].numbers}"
                    }
                    holder.itemView.layoutKC.visibility = View.GONE
                }
                GR_ORDER_OUT->{
                    if (cargoDetails.ck_status == 0) {
                        holder.itemView.cargoStatus.text = "未出库"
                    } else {
                        holder.itemView.cargoStatus.text = "已出库\n货物编号：${cargoDetails.numbers_lists[0].numbers}"
                    }
                    holder.itemView.layoutKC.visibility = View.GONE
                }
                GR_ORDER_IN->{
                    if (cargoDetails.rk_status == 0) {
                        holder.itemView.cargoStatus.text = "未入库"
                    } else {
                        holder.itemView.cargoStatus.text = "已入库\n货物编号：${cargoDetails.numbers_lists[0].numbers}"
                    }
                    holder.itemView.layoutKC.visibility = View.GONE
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
                mAdapter.notifyItemChanged(0)
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
}