package com.cyf.union.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.initActionBar
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.CustomDialog
import com.cyf.lezu.utils.LoginErrDialog
import com.cyf.lezu.utils.MyProgressDialog
import com.cyf.union.R
import com.cyf.union.entity.QD
import com.cyf.union.entity.TXSQ
import com.cyf.union.entity.getInterface
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_worker_check.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/4/15/015.
 */
class WorkerCheckActivity : MyBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_worker_check)
        loadLayout.isEnabled = false
        initActionBar(this, "签到", rightBtn = "提交", rightClick = {
            if (checkData()) {
                submit()
            }
        })
        initViews()
    }

    private fun initViews() {
        worker_check_check.setOnClickListener {
            location()
        }
    }

    private fun checkData(): Boolean {
        if (worker_check_location.text.trim().isEmpty()) {
            CustomDialog("错误", "请先定位")
            return false
        } else {
            return true
        }
    }

    private fun submit() {
        val map = mapOf(Pair("address", worker_check_location.text.toString()), Pair("contents", worker_check_beizhu.text.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                CustomDialog(message = "签到成功", positiveClicked = android.content.DialogInterface.OnClickListener { dialog, which ->
                    finish()
                })
            }

            override fun onError(context: Context, error: String) {
                toast(error)
                CustomDialog("错误", message = error)
            }

            override fun onLoginErr(context: Context) {
                LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this@WorkerCheckActivity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(this, QD.getInterface(), map)
    }

    private fun location() {

    }

}