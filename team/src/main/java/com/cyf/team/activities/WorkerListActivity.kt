package com.cyf.team.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.view.View
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.adapters.LineDecoration
import com.cyf.lezu.adapters.MyBaseAdapter
import com.cyf.lezu.initActionBar
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.CalendarUtil
import com.cyf.lezu.utils.CustomDialog
import com.cyf.lezu.utils.LoginErrDialog
import com.cyf.team.R
import com.cyf.team.entity.*
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_worker_list.*
import kotlinx.android.synthetic.main.layout_worker_item.view.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/22/022.
 */
class WorkerListActivity : MyBaseActivity() {

    val workers = ArrayList<Worker>()
    private val adapter = MyAdapter(workers)
    private var isWorker = true
    private var orderId = 0
    private var orderType = "gm"
    private var typeId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_list)
        isWorker = intent.getBooleanExtra("isWorker", true)
        orderId = intent.getIntExtra("orderId", 0)
        orderType = intent.getStringExtra("orderType")
        typeId = intent.getIntExtra("typeId", 1)
        initActionBar(this, if (isWorker) "工人列表" else "巡检列表")
        initViews()
        getWorkers()
    }

    private fun initViews() {
        val layoutManager2 = LinearLayoutManager(this)
        workerList.layoutManager = layoutManager2
        layoutManager2.orientation = OrientationHelper.VERTICAL
        workerList.addItemDecoration(LineDecoration(this, LineDecoration.VERTICAL))
        workerList.itemAnimator = DefaultItemAnimator()
        workerList.isNestedScrollingEnabled = false
        workerList.adapter = adapter
        adapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                CustomDialog("提示", "确定要安排这个工人进行当前订单吗？", positiveClicked = DialogInterface.OnClickListener { dialog, which ->
                    setWorker(workers[position].id)
                }, negative = "取消")
            }
        }
    }

    private inner class MyAdapter(val data: ArrayList<Worker>) : MyBaseAdapter(R.layout.layout_worker_item) {
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val worker = data[position]
            holder.itemView.workerItemAccount.text = worker.account
            holder.itemView.workerItemName.text = worker.title
            holder.itemView.workerItemPhone.text = worker.phone
            holder.itemView.workerItemAddress.text = worker.address
            holder.itemView.workerItemLocal.text = "${worker.lat}，${worker.lng}"
            holder.itemView.workerItemLoginTime.text = CalendarUtil(worker.login_time, true).format(CalendarUtil.STANDARD)
            holder.itemView.workerItemLocalBtn.setOnClickListener {
                val intent = Intent(it.context, LocationActivity::class.java)
                intent.putExtra("lat", worker.lat.toDouble())
                intent.putExtra("lng", worker.lng.toDouble())
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int = data.size
    }

    private fun getWorkers() {
        val map = mapOf(
                Pair("type_id", if (isWorker) "8" else "7")
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val workerListRes = Gson().fromJson(result, WorkerListRes::class.java)
                workers.clear()
                workers.addAll(workerListRes.retRes)
                adapter.notifyDataSetChanged()
            }

            override fun onError(context: Context, error: String) {
                toast(error)
            }

            override fun onLoginErr(context: Context) {
                LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, true).postRequest(this as Context, "grlists".getInterface(), map)
    }

    private fun setWorker(id: Int) {
        val map = mapOf(
                Pair("orders_type", orderType)
                , Pair("orders_id", orderId.toString())
                , Pair("gr_id", id.toString())
                , Pair("type_id", typeId.toString())
                , Pair("contents", typeId.toString())
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                toast("安排成功")
                finish()
            }

            override fun onError(context: Context, error: String) {
                toast(error)
            }

            override fun onLoginErr(context: Context) {
                LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, true).postRequest(this as Context, "setordergr".getInterface(), map)
    }
}