package com.cyf.union.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.adapters.LineDecoration
import com.cyf.lezu.entity.WorkerGrade
import com.cyf.lezu.entity.WorkerGradeListRes
import com.cyf.lezu.getTime
import com.cyf.lezu.initActionBar
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.CustomDialog
import com.cyf.lezu.utils.LoginErrDialog
import com.cyf.union.R
import com.cyf.union.entity.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_worker_grade.*
import kotlinx.android.synthetic.main.grade_details_list_item.view.*
import java.util.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/4/30/030.
 */
class WorkerGradeActivity : MyBaseActivity() {
    var data = ArrayList<WorkerGrade>()
    val adapter = MyAdapter(data)
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grade)
        initActionBar(this, "员工绩效", rightBtn = "实付记录", rightClick = {
            val intent = Intent(this, RealPayLogActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        })
        id = intent.getIntExtra("id", 0)
        initRecyclerView()
        getWorkerGradeDetails(id)
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        gradeRecyclerView.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        adapter.red = resources.getColor(R.color.red_text)
        adapter.black = resources.getColor(android.R.color.black)
        gradeRecyclerView.adapter = adapter
        gradeRecyclerView.addItemDecoration(LineDecoration(this, LineDecoration.VERTICAL))
        gradeRecyclerView.itemAnimator = DefaultItemAnimator()
        gradeRecyclerView.isNestedScrollingEnabled = false
        adapter.onItemClickListener = object : MyAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (view.id == R.id.details) {
                    val intent = Intent(this@WorkerGradeActivity, OrderDetailsActivity::class.java)
                    intent.putExtra("id", data[position].id)
                    intent.putExtra("type", data[position].type)
                    startActivity(intent)
                } else {
                    val price = data[position].backUpPrice
                    if (price < 0) {
                        toast("请输入金额")
                    } else {
                        CustomDialog(message = "确定提交金额为：${price}?", positiveClicked = DialogInterface.OnClickListener { _, _ ->
                            submitPrice(data[position].ztyg_id, data[position].id, data[position].type, price, data[position].jxyff == 1)
                        })
                    }
                }
            }
        }
    }

    private fun getWorkerGradeDetails(id: Int) {
        val map = mapOf(Pair("yg_id", id.toString()), Pair("jxyff", "0"))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val workerGrade = Gson().fromJson(result, WorkerGradeListRes::class.java)
                data.clear()
                data.addAll(workerGrade.retRes)
                adapter.notifyDataSetChanged()
            }

            override fun onError(context: Context, error: String) {
                toast(error)
                CustomDialog("错误", message = error)
            }

            override fun onLoginErr(context: Context) {
                LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this@WorkerGradeActivity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        },false).postRequest(this, YGJX.getInterface(), map)
    }

    fun submitPrice(yg_id: Int, orders_id: Int, orders_type: String, price: Double, isChange: Boolean) {
        val map = mapOf(Pair("yg_id", yg_id.toString()), Pair("orders_id", orders_id.toString()), Pair("orders_type", orders_type)
                , Pair("price", price.toString()), Pair("contents", ""))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                getWorkerGradeDetails(id)
            }

            override fun onError(context: Context, error: String) {
                toast(error)
                CustomDialog("错误", message = error)
            }

            override fun onLoginErr(context: Context) {
                LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this@WorkerGradeActivity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        },false).postRequest(this,  if (isChange) XGJX.getInterface() else FFJX.getInterface(), map)
    }


    class MyAdapter(val data: ArrayList<WorkerGrade>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        var onItemClickListener: OnItemClickListener? = null
        var red = 0
        var black = 0

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val workGrade = data[position]

            holder.itemView.orderCount.setTextColor(red)
            holder.itemView.orderYF.setTextColor(black)
            holder.itemView.layout_edit.visibility = View.VISIBLE
            holder.itemView.details.visibility = View.VISIBLE
            holder.itemView.faFangTime.visibility = View.VISIBLE
            holder.itemView.queRen.visibility = View.GONE

            holder.itemView.orderNum.text = "订单编号：${workGrade.numbers}"
            holder.itemView.userName.text = "用户名称：${workGrade.title}"
            holder.itemView.userAccount.text = "用户账号：${workGrade.account}"
            holder.itemView.orderCount.text = "成交数量：${workGrade.goods_num}   成交总额：${workGrade.price}"
            holder.itemView.orderYF.text = "应发放额：${workGrade.jx_price}"
            holder.itemView.faFangTime.text = if (workGrade.jxff_time <= 0) "" else "绩效发放时间 ：${getTime(workGrade.jxff_time)}"
            holder.itemView.zl_or_gm.text = "${if (workGrade.type == "zl") "租" else "购"}"
            if (workGrade.jxyff == 0) {
                holder.itemView.submit.text = "提交"
                holder.itemView.submit.setBackgroundResource(R.drawable.button_gray)
            } else {
                holder.itemView.submit.text = "修改"
                holder.itemView.submit.setBackgroundResource(R.drawable.button_gray_drak)
            }

            if (workGrade.jx_price == workGrade.backUpPrice) {
                holder.itemView.edit_price.setText("")
            }

            holder.itemView.details.setOnClickListener {
                onItemClickListener?.onItemClick(it, position)
            }
            holder.itemView.submit.setOnClickListener {
                data[position].backUpPrice = if (holder.itemView.edit_price.text.isEmpty()) {
                    -1.0
                } else {
                    holder.itemView.edit_price.text.toString().toDouble()
                }
                onItemClickListener?.onItemClick(it, position)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.grade_details_list_item, parent, false))
        }

        interface OnItemClickListener {
            fun onItemClick(view: View, position: Int)
        }

        override fun getItemCount(): Int = data.size

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

}