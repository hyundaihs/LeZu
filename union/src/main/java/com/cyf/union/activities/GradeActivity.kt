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
import com.cyf.lezu.D
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.adapters.LineDecoration
import com.cyf.lezu.entity.RequestResult
import com.cyf.lezu.entity.WorkerGrade
import com.cyf.lezu.entity.WorkerGradeListRes
import com.cyf.lezu.initActionBar
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.CustomDialog
import com.cyf.lezu.utils.LoginErrDialog
import com.cyf.union.R
import com.cyf.union.R.id.queRen
import com.cyf.union.fragments.WorkGradeFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_grade.*
import kotlinx.android.synthetic.main.grade_details_list_item.view.*
import java.util.*

/**
 * Created by kevin on 2018/6/12.
 */
class GradeActivity : MyBaseActivity() {

    var data = ArrayList<WorkerGrade>()
    val adapter = MyAdapter(data)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grade)
        initActionBar(this, "员工绩效")
        initRecyclerView()
        getWorkerGradeDetails()
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
                CustomDialog(message = "确定收款吗？", positiveClicked = DialogInterface.OnClickListener { _, _ ->
                    queRen(position)
                })
            }
        }
    }

    private fun getWorkerGradeDetails() {
        val map = mapOf(Pair("jxyff", "0"))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(result: String) {
                val workerGrade = Gson().fromJson(result, WorkerGradeListRes::class.java)
                data.clear()
                data.addAll(workerGrade.retRes)
                adapter.notifyDataSetChanged()
            }

            override fun onError(error: String) {
                toast(error)
            }

            override fun onLoginErr() {
                LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this@GradeActivity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(this, MySimpleRequest.YGJXYG, map)
    }

    private fun queRen(position: Int) {
        val workGrade = data[position]
        val map = mapOf(Pair("orders_id", "${workGrade.id}"), Pair("orders_type", workGrade.type))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(result: String) {
                data[position].jxyqr = 1
                adapter.notifyDataSetChanged()
            }

            override fun onError(error: String) {
                toast(error)
            }

            override fun onLoginErr() {
                LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this@GradeActivity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(this, MySimpleRequest.QDJX, map)
    }

    class MyAdapter(val data: ArrayList<WorkerGrade>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        var onItemClickListener: OnItemClickListener? = null
        var red = 0
        var black = 0

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val workGrade = data[position]

            holder.itemView.orderCount.setTextColor(black)
            holder.itemView.orderYF.setTextColor(red)
            holder.itemView.layout_edit.visibility = View.GONE
            holder.itemView.details.visibility = View.GONE
            holder.itemView.faFangTime.visibility = View.GONE
            holder.itemView.queRen.visibility = View.VISIBLE

            holder.itemView.orderNum.text = "订单编号：${workGrade.numbers}"
            holder.itemView.userName.text = "用户名称：${workGrade.title}"
            holder.itemView.userAccount.text = "用户账号：${workGrade.account}"
            holder.itemView.orderCount.text = "成交数量：${workGrade.goods_num}   成交总额：${workGrade.price}"
            holder.itemView.orderYF.text = "应发放额：${workGrade.jx_price}"
            holder.itemView.zl_or_gm.text = "${if (workGrade.type == "zl") "租" else "购"}"
            holder.itemView.queRen.visibility = if (workGrade.jxyqr == 0) View.VISIBLE else View.GONE
            holder.itemView.queRen.setOnClickListener {
                onItemClickListener?.onItemClick(it, position)
            }
        }

        fun setViews(isBoss: Boolean, holder: WorkerGradeActivity.MyAdapter.MyViewHolder) {
            if (isBoss) {
                holder.itemView.orderCount.setTextColor(red)
                holder.itemView.orderYF.setTextColor(black)
                holder.itemView.layout_edit.visibility = View.VISIBLE
                holder.itemView.details.visibility = View.VISIBLE
                holder.itemView.faFangTime.visibility = View.VISIBLE
                holder.itemView.queRen.visibility = View.GONE
            } else {
                holder.itemView.orderCount.setTextColor(black)
                holder.itemView.orderYF.setTextColor(red)
                holder.itemView.layout_edit.visibility = View.GONE
                holder.itemView.details.visibility = View.GONE
                holder.itemView.faFangTime.visibility = View.GONE
                holder.itemView.queRen.visibility = View.VISIBLE
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