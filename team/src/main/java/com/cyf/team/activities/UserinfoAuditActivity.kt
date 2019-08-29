package com.cyf.team.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.initActionBar
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.CalendarUtil
import com.cyf.lezu.utils.LoginErrDialog
import com.cyf.team.AppTeam
import com.cyf.team.R
import com.cyf.team.entity.*
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_userinfo_audit.*
import kotlinx.android.synthetic.main.fragment_mine_kg.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/16/016.
 */
class UserinfoAuditActivity : MyBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo_audit)
        initViews(AppTeam.userInfo)
    }

    private fun initViews(userInfo: UserInfo) {
        initActionBar(this, userInfo.title, true)
        auditTime.text = "提交时间：${CalendarUtil(userInfo.zl_time, true).format(CalendarUtil.STANDARD)}"
        auditName.text = "昵称：${userInfo.title}"
        auditAccount.text = "账号：${userInfo.account}"
        auditType.text = "账户类型：${if (userInfo.type_id == 1) "个人" else "企业"}"
        auditIDCard.text = if (userInfo.type_id == 1) {
            "身份证号：${userInfo.id_numbers}"
        } else {
            "营业执照号码：${userInfo.id_numbers}"
        }
        auditLinkman.text = if (userInfo.type_id == 1) {
            "联系人：${userInfo.link_man}"
        } else {
            "公司法人：${userInfo.fr_name}"
        }
        auditLinkPhone.text = if (userInfo.type_id == 1) {
            "联系电话：${userInfo.link_phone}"
        } else {
            "法人电话：${userInfo.fr_link_phone}"
        }
        if (userInfo.type_id == 1) {
            Picasso.with(this).load(IMAGE_URL + userInfo.id_file_url1).into(auditImage1)
            Picasso.with(this).load(IMAGE_URL + userInfo.id_file_url2).into(auditImage2)
            auditLine2.visibility = View.VISIBLE
            auditImage2.visibility = View.VISIBLE
        } else {
            Picasso.with(this).load(IMAGE_URL + userInfo.yyzz_file_url).into(auditImage1)
            auditLine2.visibility = View.GONE
            auditImage2.visibility = View.GONE
        }
        auditOk.setOnClickListener {
            handleRel(userInfo.id, true)
        }

        auditNo.setOnClickListener {
            handleRel(userInfo.id, false)
        }
    }

    private fun handleRel(id: Int, isOk: Boolean) {
        val map = mapOf(
                Pair("id", id.toString())
                , Pair("zl_status", if (isOk) "2" else "3")
                , Pair("zl_contents", auditContent.text.toString())
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                toast("操作成功")
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

        }, false).postRequest(this as Context, ACCZLSET.getInterface(), map)
    }
}