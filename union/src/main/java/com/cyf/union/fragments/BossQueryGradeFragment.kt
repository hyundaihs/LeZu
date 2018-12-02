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
import com.cyf.lezu.adapters.LineDecoration
import com.cyf.lezu.entity.StoreInfoBoss
import com.cyf.lezu.entity.StoreInfoBossRes
import com.cyf.lezu.entity.WorkerInfo
import com.cyf.lezu.entity.WorkerListRes
import com.cyf.lezu.fragments.BaseFragment
import com.cyf.lezu.initActionBar
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.LoginErrDialog
import com.cyf.union.AppUnion
import com.cyf.union.AppUnion.Companion.workerList
import com.cyf.union.R
import com.cyf.union.activities.LoginActivity
import com.cyf.union.activities.TiXianActivity
import com.cyf.union.activities.WorkerGradeActivity
import com.cyf.union.entity.IMAGE_URL
import com.cyf.union.entity.YG_LISTS
import com.cyf.union.entity.ZT_INFO
import com.cyf.union.entity.getInterface
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_boss_query_grade.*
import kotlinx.android.synthetic.main.work_grade_list_item.view.*


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/1/6/006.
 * Boss绩效查询
 */
class BossQueryGradeFragment() : BaseFragment() {
    lateinit var storeInfoBoss: StoreInfoBoss
    private var adapter = MyAdapter(workerList)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_boss_query_grade, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.initActionBar(activity as AppCompatActivity, "员工绩效", false, "退出", {
            AppUnion.isLogged = false
            activity?.finish()
        }, "提现", {
            val intent = Intent(activity, TiXianActivity::class.java)
            intent.putExtra("price", storeInfoBoss.ye_price)
            startActivity(intent)
        })
        getStoreInfo()
        getWorkerList()
    }

    /**
     * 获取店铺信息
     */
    private fun getStoreInfo() {
        val map = mapOf(Pair("", ""))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val storeInfoBossRes = Gson().fromJson(result, StoreInfoBossRes::class.java)
                storeInfoBoss = storeInfoBossRes.retRes
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

        }, false).postRequest(activity as Context, ZT_INFO.getInterface(), map)
    }

    private fun initViews() {
        AppUnion.edu = storeInfoBoss.syyhq.toInt()
        boss_query_grade_yue.setText("提现余额:\n¥${storeInfoBoss.ye_price}")
        Picasso.with(activity).load(IMAGE_URL + storeInfoBoss.ewm_file_url).into(boss_query_grade_ewm_pic)
        boss_query_grade_ewm.setText("店铺二维码:${storeInfoBoss.id}")
        initRecyclerView()
    }

    /**
     * 获取员工列表
     */
    private fun getWorkerList() {
        val map = mapOf(Pair("", ""))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val workerListRes = Gson().fromJson(result, WorkerListRes::class.java)
                workerList.clear()
                workerList.addAll(workerListRes.retRes)
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

        }, false).postRequest(activity as Context, YG_LISTS.getInterface(), map)
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(activity)
        boss_query_grade_RecyclerView.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        boss_query_grade_RecyclerView.adapter = adapter
        boss_query_grade_RecyclerView.addItemDecoration(LineDecoration(activity, LineDecoration.VERTICAL))
        boss_query_grade_RecyclerView.itemAnimator = DefaultItemAnimator()
        boss_query_grade_RecyclerView.isNestedScrollingEnabled = false
        adapter.onItemClickListener = object : MyAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(activity, WorkerGradeActivity::class.java)
                intent.putExtra("id", workerList[position].id)
                startActivity(intent)
            }
        }
    }

    private class MyAdapter(val data: ArrayList<WorkerInfo>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        var onItemClickListener: OnItemClickListener? = null

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val workerInfo = data[position]
            holder.itemView.work_grade_list_item_name.text = workerInfo.title
            holder.itemView.work_grade_list_item_account.text = workerInfo.phone
            holder.itemView.work_grade_list_item_number.text = workerInfo.orders_num.toString()
            holder.itemView.work_grade_list_item_amount.text = workerInfo.orders_price.toString()
            holder.itemView.work_grade_list_item_type.visibility = View.GONE
            holder.itemView.setOnClickListener {
                onItemClickListener?.onItemClick(it, position)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.work_grade_list_item, parent, false))
        }

        override fun getItemCount(): Int = data.size

        interface OnItemClickListener {
            fun onItemClick(view: View, position: Int)
        }

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

}
