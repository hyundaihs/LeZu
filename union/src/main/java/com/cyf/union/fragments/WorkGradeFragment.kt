package com.cyf.union.fragments

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
import com.cyf.lezu.*
import com.cyf.lezu.adapters.LineDecoration
import com.cyf.lezu.entity.*
import com.cyf.lezu.fragments.BaseFragment
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.utils.*
import com.cyf.union.AppUnion
import com.cyf.union.R
import com.cyf.union.ShowImageDialog
import com.cyf.union.activities.*
import com.cyf.union.entity.IMAGE_URL
import com.cyf.union.entity.YGINFO
import com.cyf.union.entity.YGJXYG
import com.cyf.union.entity.getInterface
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_faquan_log.*
import kotlinx.android.synthetic.main.fragment_worker_grade.*
import kotlinx.android.synthetic.main.layout_wx_share.view.*
import kotlinx.android.synthetic.main.work_grade_details_list_item.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/1/6/006.
 * 员工绩效
 */
class WorkGradeFragment() : BaseFragment() {
    lateinit var workerDetails: WorkerDetails
    var data = ArrayList<WorkerGrade>()
    val adapter = MyAdapter(data)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_worker_grade, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.initActionBar(activity as AppCompatActivity, "绩效", false, "退出", {
            AppUnion.isLogged = false
            activity?.finish()
        }, "绩效记录", {
            val intent = Intent(activity, GradeActivity::class.java)
            startActivity(intent)
        })
        work_grade_qianDao.setOnClickListener {
            startActivity(Intent(activity, LocationActivity::class.java))
        }
        getWorkerDetails()
        initRecyclerView()
    }

    private fun getWorkerDetails() {
        val map = mapOf(Pair("", ""))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val workerDetailsRes = Gson().fromJson(result, WorkerDetailsRes::class.java)
                workerDetails = workerDetailsRes.retRes
                getWorkerGradeDetails()
                initViews()
            }

            override fun onError(context: Context, error: String) {
                activity?.toast(error)
            }

            override fun onLoginErr(context: Context) {
                activity?.LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, false).postRequest(activity as Context, YGINFO.getInterface(), map)
    }

    private fun getWorkerGradeDetails() {
        val map = mapOf(Pair("jxyff", "0"))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val workerGrade = Gson().fromJson(result, WorkerGradeListRes::class.java)
                data.clear()
                data.addAll(workerGrade.retRes)
                adapter.notifyDataSetChanged()
            }

            override fun onError(context: Context, error: String) {
                activity?.toast(error)
            }

            override fun onLoginErr(context: Context) {
                activity?.LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, false).postRequest(activity as Context, YGJXYG.getInterface(), map)
    }

    private fun initViews() {
//        work_grade_ewm_title.setText("我的二维码:${workerDetails.zt_id}")
        work_grade_my_ticheng.setText("绩效总额:\n¥${workerDetails.yffjx}")
        val picasso = Picasso.with(activity)
        picasso.load(IMAGE_URL + workerDetails.ewm_file_url).into(work_grade_ewm_pic)
        work_grade_ewm_pic.setOnLongClickListener() {
            val view = LayoutInflater.from(activity as Context).inflate(R.layout.layout_wx_share, null, false)
            val dialog = (activity as Context).BottomDialog(view)

            view.pyq.setOnClickListener {
                dialog.dismiss()
                val pDialog = MyProgressDialog(activity as Context)
                doAsync {
                    val bitmap = picasso.load(IMAGE_URL + workerDetails.ewm_file_url).get()
                    uiThread {
                        val rel = AppUnion.instance.api.sendBitmapToWx(bitmap, true)
                        pDialog.dismiss()
                        D("发送${if (rel) "成功" else "失败"}")
                    }
                }
            }
            view.wxhy.setOnClickListener {
                dialog.dismiss()
                val pDialog = MyProgressDialog(activity as Context)
                doAsync {
                    val bitmap = picasso.load(IMAGE_URL + workerDetails.ewm_file_url).get()
                    uiThread {
                        val rel = AppUnion.instance.api.sendBitmapToWx(bitmap, false)
                        D("发送${if (rel) "成功" else "失败"}")
                        pDialog.dismiss()
                    }
                }
            }
            view.ewm.setOnClickListener {
                dialog.dismiss()
                if (null != WorkSalePlatFragment.workerDetails)
                    (activity as Context).ShowImageDialog(WorkSalePlatFragment.workerDetails!!.ewm_file_url)
            }
            true
        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(activity)
        workerGradeRecyclerView.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        workerGradeRecyclerView.adapter = adapter
        workerGradeRecyclerView.addItemDecoration(LineDecoration(activity, LineDecoration.VERTICAL))
        workerGradeRecyclerView.itemAnimator = DefaultItemAnimator()
        workerGradeRecyclerView.isNestedScrollingEnabled = false
        adapter.onItemClickListener = object : MyAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(activity, OrderDetailsActivity::class.java)
                intent.putExtra("id", data[position].id)
                intent.putExtra("type", data[position].type)
                startActivity(intent)
            }
        }
    }

    class MyAdapter(val data: ArrayList<WorkerGrade>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        var onItemClickListener: OnItemClickListener? = null

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val workGrade = data[position]
            holder.itemView.createTime.text = "创建时间：${getTime(workGrade.create_time)}"
            holder.itemView.userName.text = "用户名称：${workGrade.title}"
            holder.itemView.account.text = "用户账号：${workGrade.account}"
            holder.itemView.orderNum.text = "购买数量：${workGrade.goods_num}"
            holder.itemView.totalPrice.text = "消费总金额：${workGrade.price}"
            holder.itemView.payType.text = "消费类型：${if (workGrade.type == "zl") "租赁" else "购买"}"
            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(it, position)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.work_grade_details_list_item, parent, false))
        }

        interface OnItemClickListener {
            fun onItemClick(view: View, position: Int)
        }

        override fun getItemCount(): Int = data.size

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }


}

