package com.cyf.union.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.adapters.LineDecoration
import com.cyf.lezu.entity.*
import com.cyf.lezu.getTime
import com.cyf.lezu.initActionBar
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.LoginErrDialog
import com.cyf.union.R
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.work_saleplat_list_item.view.*
import java.util.ArrayList

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/4/30/030.
 */
class OrderDetailsActivity : MyBaseActivity() {

    val data = ArrayList<GoodsNoId>()
    private val adapter = MyAdapter(data)
    var id = 0
    var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
        initActionBar(this, "成交详情")
        id = intent.getIntExtra("id", 0)
        type = intent.getStringExtra("type")
        getOrderDetails()
        initRecyclerView()
    }

    private fun refreshViews(ordersDetails: OrdersDetails) {
        orderNum.text = "订单编号：${ordersDetails.numbers}"
        totalPrice.text = "产品总价：${ordersDetails.goods_price}"
        orderYaJin.text = "订单押金：${ordersDetails.yj_price}"
        orderZuJin.text = "订单租金：${ordersDetails.zj_price}"
        payedPrice.text = "已付金额：${ordersDetails.price}"
        userName.text = "客户姓名：${ordersDetails.title}"
        userPhone.text = "客户电话：${ordersDetails.phone}"
        pinJiaTime.text = "评价时间：${getTime(ordersDetails.pj_time)}"
        pinJiaContent.text = "评价内容：${ordersDetails.pj_contents}"
        address.text = "收货地址：${ordersDetails.pca}${ordersDetails.address}"


        data.clear()
        data.addAll(ordersDetails.lists)
        adapter.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        orderDetailsRecyclerView.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        orderDetailsRecyclerView.adapter = adapter
        orderDetailsRecyclerView.addItemDecoration(LineDecoration(this, LineDecoration.VERTICAL))
        orderDetailsRecyclerView.itemAnimator = DefaultItemAnimator()
        orderDetailsRecyclerView.isNestedScrollingEnabled = false
    }

    private fun getOrderDetails() {
        val map = mapOf(Pair("orders_id", id.toString()), Pair("type", type))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(result: String) {
                val orderDetailsRes = Gson().fromJson(result, OrdersDetailsRes::class.java)
                val ordersDetails = orderDetailsRes.retRes
                refreshViews(ordersDetails)
            }

            override fun onError(error: String) {
                toast(error)
            }

            override fun onLoginErr() {
                LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this@OrderDetailsActivity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(this as Context, MySimpleRequest.ORDER_INFO, map)
    }

    private class MyAdapter(val data: ArrayList<GoodsNoId>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val goods = data[position]
            Picasso.with(holder.itemView.context).load(MySimpleRequest.IMAGE_URL + goods.file_url).into(holder.itemView.goodsImage)
            holder.itemView.goodsName.text = goods.title
            holder.itemView.goodsRent.text = "租赁:¥${goods.zl_price1}-¥${goods.zl_price2}/月"
//            holder.itemView.goodsDeposit.text = "押金:¥${goods.deposit}"
            holder.itemView.goodsPrice.text = "购买:¥${goods.price}"
            holder.itemView.share.visibility = View.GONE
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.work_saleplat_list_item, parent, false))
        }

        override fun getItemCount(): Int = data.size

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}