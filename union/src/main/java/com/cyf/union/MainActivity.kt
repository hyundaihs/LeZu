package com.cyf.union

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.cyf.lezu.D
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.entity.SystemInfoRes
import com.cyf.lezu.requestPermission
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.requests.MySimpleRequest.Companion.SYS_INFO
import com.cyf.lezu.toast
import com.cyf.union.activities.LoginActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/1/4/004.
 */
class MainActivity : MyBaseActivity(), View.OnClickListener {
    override fun onClick(v: View) {
        when (v.id) {
            R.id.startWork -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            R.id.introduce -> {
            }
            R.id.newsInfo -> {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission(onDenied = {
            finish()
        })

        getSystemInfo()
        setContentView(R.layout.activity_main)
        startWork.setOnClickListener(this)
        introduce.setOnClickListener(this)
        newsInfo.setOnClickListener(this)
    }

    private fun getSystemInfo() {
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(result: String) {
                val systemInfoRes = Gson().fromJson(result, SystemInfoRes::class.java)
                AppUnion.systemInfo = systemInfoRes.retRes
            }

            override fun onError(error: String) {
                D("onError = ")
                toast(error)
            }

            override fun onLoginErr() {

            }

        }).postRequest(this, SYS_INFO, mapOf())
    }
}