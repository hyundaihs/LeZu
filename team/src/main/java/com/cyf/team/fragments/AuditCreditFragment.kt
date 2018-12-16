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
import com.cyf.lezu.adapters.LineDecoration
import com.cyf.lezu.adapters.MyBaseAdapter
import com.cyf.lezu.fragments.BaseFragment
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.utils.CalendarUtil
import com.cyf.team.AppTeam
import com.cyf.team.R
import com.cyf.team.activities.CreditAuditActivity
import com.cyf.team.entity.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_audit.*
import kotlinx.android.synthetic.main.fragment_orders.*
import kotlinx.android.synthetic.main.layout_audit_userinfo_item.view.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/16/016.
 */
class AuditCreditFragment : BaseFragment() {

    private val creditInfos = ArrayList<CreditInfo>()
    private val creditAdapter = MyUserInfoAdapter(creditInfos)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_audit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        getCreditrInfoData()
    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(context)
        auditList.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        auditList.addItemDecoration(LineDecoration(context, LineDecoration.VERTICAL))
        auditList.itemAnimator = DefaultItemAnimator()
        auditList.isNestedScrollingEnabled = false
        auditList.adapter = creditAdapter
        auditSearch.setOnClickListener {
            getCreditrInfoData()
        }
        creditAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                AppTeam.creditInfo = creditInfos[position].copy()
                startActivity(Intent(view.context, CreditAuditActivity::class.java))
            }
        }
    }

    private inner class MyUserInfoAdapter(val data: ArrayList<CreditInfo>) : MyBaseAdapter(R.layout.layout_audit_userinfo_item) {
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val creditInfo = data[position]
            holder.itemView.auditItemName.text = "名称：${creditInfo.title}"
            holder.itemView.auditItemTime.text = "提交时间：${CalendarUtil(creditInfo.zl_time, true).format(CalendarUtil.STANDARD)}"
            holder.itemView.auditItemAccount.text = "账号：${creditInfo.account}"
            holder.itemView.auditItemType.text = "账号类型：${if (creditInfo.type_id == 1) "个人" else "企业"}"
            holder.itemView.auditStatus.visibility = View.GONE
        }

        override fun getItemCount(): Int = data.size
    }

    private fun getCreditrInfoData() {
        val map = mapOf(
                Pair("account", if (auditAccount.text.isEmpty()) "" else auditAccount.text.toString())
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val creditInfoListRes = Gson().fromJson(result, CreditInfoListRes::class.java)
                creditInfos.clear()
                creditInfos.addAll(creditInfoListRes.retRes)
                creditAdapter.notifyDataSetChanged()
            }

            override fun onError(context: Context, error: String) {
                ordersSwipe?.isRefreshing = false
            }

            override fun onLoginErr(context: Context) {
                ordersSwipe?.isRefreshing = false
            }

        }, false).postRequest(activity as Context, XYSH.getInterface(), map)
    }
}