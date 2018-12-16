package com.cyf.team.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cyf.lezu.adapters.LineDecoration
import com.cyf.lezu.adapters.MyBaseAdapter
import com.cyf.lezu.fragments.BaseFragment
import com.cyf.lezu.initActionBar
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.CalendarUtil
import com.cyf.team.AppTeam
import com.cyf.team.R
import com.cyf.team.activities.CreditAuditActivity
import com.cyf.team.activities.UserinfoAuditActivity
import com.cyf.team.entity.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_audit.*
import kotlinx.android.synthetic.main.fragment_audit.view.*
import kotlinx.android.synthetic.main.fragment_mine_kg.*
import kotlinx.android.synthetic.main.fragment_orders.*
import kotlinx.android.synthetic.main.layout_audit_userinfo_item.view.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/16/016.
 */
class AuditUserInfoFragment : BaseFragment() {

    private val userInfos = ArrayList<UserInfo>()
    private val userAdapter = MyUserInfoAdapter(userInfos)
    private val STATUS = listOf("未提交", "审核中", "通过", "未通过")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_audit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
        getUserInfoData()
    }

    private fun initViews() {
        val layoutManager = LinearLayoutManager(context)
        auditList.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        auditList.addItemDecoration(LineDecoration(context, LineDecoration.VERTICAL))
        auditList.itemAnimator = DefaultItemAnimator()
        auditList.isNestedScrollingEnabled = false
        auditList.adapter = userAdapter
        auditSearch.setOnClickListener {
            getUserInfoData()
        }
        userAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                AppTeam.userInfo = userInfos[position].copy()
                startActivity(Intent(view.context, UserinfoAuditActivity::class.java))
            }
        }
    }

    private inner class MyUserInfoAdapter(val data: ArrayList<UserInfo>) : MyBaseAdapter(R.layout.layout_audit_userinfo_item) {
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val userInfo = data[position]
            holder.itemView.auditItemName.text = "名称：${userInfo.title}"
            holder.itemView.auditItemTime.text = "提交时间：${CalendarUtil(userInfo.zl_time, true).format(CalendarUtil.STANDARD)}"
            holder.itemView.auditItemAccount.text = "账号：${userInfo.account}"
            holder.itemView.auditItemType.text = "账号类型：${if (userInfo.type_id == 1) "个人" else "企业"}"
            holder.itemView.auditStatus.text = STATUS[userInfo.zl_status]
        }

        override fun getItemCount(): Int = data.size
    }

    private fun getUserInfoData() {
        val map = mapOf(
                Pair("account", if (auditAccount.text.isEmpty()) "" else auditAccount.text.toString())
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val userInfoListRes = Gson().fromJson(result, UserInfoListRes::class.java)
                userInfos.clear()
                userInfos.addAll(userInfoListRes.retRes)
                userAdapter.notifyDataSetChanged()
            }

            override fun onError(context: Context, error: String) {
                ordersSwipe?.isRefreshing = false
            }

            override fun onLoginErr(context: Context) {
                ordersSwipe?.isRefreshing = false
            }

        }, false).postRequest(activity as Context, ACCDSH.getInterface(), map)
    }
}