package com.cyf.union.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cyf.lezu.D
import com.cyf.lezu.adapters.LineDecoration
import com.cyf.lezu.entity.YHQInfo
import com.cyf.lezu.entity.YHQInfoListRes
import com.cyf.lezu.fragments.BaseFragment
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.CalendarUtil
import com.cyf.lezu.utils.LoginErrDialog
import com.cyf.union.AppUnion
import com.cyf.union.R
import com.cyf.union.UserID
import com.cyf.union.activities.LoginActivity
import com.cyf.union.adapters.EndLessOnScrollListener
import com.cyf.union.entity.YHQ_LISTS
import com.cyf.union.entity.YHQ_LISTS_YG
import com.cyf.union.entity.getInterface
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_faquan_log.*
import kotlinx.android.synthetic.main.layout_faquan_log_list_item.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/4/14/014.
 */
class FaquanLogFragment : BaseFragment() {

    val data = ArrayList<YHQInfo>()
    private val adapter = MyAdapter(data)
    var type: Int = 0
    var isFirst = false
    var endLessOnScrollListener = object : EndLessOnScrollListener() {
        override fun onLoadMore(currentPage: Int) {
            getYHQInfo(true, currentPage)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_faquan_log, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        type = arguments?.getInt("type", 1)!!
        initListViews()
    }

    private fun initListViews() {
        val layoutManager = LinearLayoutManager(activity)
        faquan_log_recycler_view.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        faquan_log_recycler_view.adapter = adapter
        faquan_log_recycler_view.addItemDecoration(LineDecoration(activity, LineDecoration.VERTICAL))
        faquan_log_recycler_view.itemAnimator = DefaultItemAnimator()
        endLessOnScrollListener.setmLinearLayoutManager(layoutManager)
        faquan_log_recycler_view.addOnScrollListener(endLessOnScrollListener)
        faquan_log_swipe_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getYHQInfo(true, 1)
            endLessOnScrollListener.currentPage = 1
        })
        getYHQInfo(true, 1)
        endLessOnScrollListener.currentPage = 1
        if (type == 1 && !isFirst) {
            isFirst = true
            doAsync {
                Thread.sleep(1000)
                uiThread {
                    getYHQInfo(true, 1)
                    endLessOnScrollListener.currentPage = 1
                }
            }
        }
    }

    private fun getYHQInfo(isRefresh: Boolean, page: Int) {
        /**
         * type_id:类型（1:优惠券，2：押金券）
        price:金额
        ztyg_id:员工id
        page：页码（为空时默认为1）
        pagesize：分页大小（为空时默认为20）
        status：状态（1：未使用，2：已使用，3：已过期）
         */
        val map = mapOf(Pair("page", page.toString()),
                Pair("status", type.toString())
        )
        val inter = if (AppUnion.logier_id == UserID.WORKER) YHQ_LISTS_YG else YHQ_LISTS
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val yhqInfoListRes = Gson().fromJson(result, YHQInfoListRes::class.java)
                if (isRefresh) {
                    data.clear()
                }
                data.addAll(yhqInfoListRes.retRes)
                adapter.notifyDataSetChanged()
                D("refresh   " + data.toString())
                faquan_log_swipe_layout.isRefreshing = false
            }

            override fun onError(context: Context, error: String) {
                activity?.toast(error)
                faquan_log_swipe_layout.isRefreshing = false
            }

            override fun onLoginErr(context: Context) {
                activity?.LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                })
                faquan_log_swipe_layout.isRefreshing = false
            }

        }, false).postRequest(activity as Context, inter.getInterface(), map)
    }

    private class MyAdapter(val data: ArrayList<YHQInfo>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val yhqInfo = data[position]
            holder.itemView.faquan_log_price.text = yhqInfo.title
            holder.itemView.faquan_log_title.text = "用户:${yhqInfo.account_title}"
            holder.itemView.faquan_log_phone.text = "手机:${yhqInfo.account}"
            val ca = CalendarUtil(yhqInfo.end_time * 1000)
            holder.itemView.faquan_log_time.text = ca.format(CalendarUtil.STANDARD)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_faquan_log_list_item, parent, false))
        }

        override fun getItemCount(): Int = data.size

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}