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
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_worker_grade.*
import kotlinx.android.synthetic.main.grade_details_simple_list_item.view.*
import java.util.ArrayList

/**
 * Created by kevin on 2018/6/12.
 */
class RealPayLogActivity : MyBaseActivity() {

    var data = ArrayList<WorkerGrade>()
    val adapter = MyAdapter(data)
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grade)
        initActionBar(this, "员工绩效实付记录")
        id = intent.getIntExtra("id", 0)
        initRecyclerView()
        getWorkerGradeDetails(id)
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        gradeRecyclerView.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        gradeRecyclerView.adapter = adapter
        gradeRecyclerView.addItemDecoration(LineDecoration(this, LineDecoration.VERTICAL))
        gradeRecyclerView.itemAnimator = DefaultItemAnimator()
        gradeRecyclerView.isNestedScrollingEnabled = false
    }

    private fun getWorkerGradeDetails(id: Int) {
        val map = mapOf(Pair("yg_id", id.toString()), Pair("jxyff", "0"))
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
                    val intent = Intent(this@RealPayLogActivity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(this as Context, MySimpleRequest.YGJX, map)
    }

    class MyAdapter(val data: ArrayList<WorkerGrade>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        var onItemClickListener: OnItemClickListener? = null

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val workGrade = data[position]

            holder.itemView.orderNum.text = "订单编号：${workGrade.numbers}"
            holder.itemView.yinFa.text = "应发放额：${workGrade.jx_price}"
            holder.itemView.updateTime.text = if (workGrade.jxff_time <= 0) "" else "绩效发放时间 ：${getTime(workGrade.jxff_time)}"
            if (workGrade.jxyff == 0) {
                holder.itemView.status.text = "待付"
                holder.itemView.status.setBackgroundResource(R.drawable.button_red)
            } else {
                holder.itemView.status.text = "已付"
                holder.itemView.status.setBackgroundResource(R.drawable.share_green)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.grade_details_simple_list_item, parent, false))
        }

        interface OnItemClickListener {
            fun onItemClick(view: View, position: Int)
        }

        override fun getItemCount(): Int = data.size

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}