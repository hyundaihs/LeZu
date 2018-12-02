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
import com.cyf.union.entity.CACCOUNT
import com.cyf.union.entity.getInterface
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_change_phone.*
import org.jetbrains.anko.toast

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/7/17/017.
 */
class ChangePhoneActivity : MyBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_phone)
        initActionBar(this, "更换绑定手机")
        changePhone_submit.setOnClickListener {
            if (checkViews()) {
                submit()
            }
        }
    }

    fun checkViews(): Boolean {
        when {
            old_phone.text.toString().trim().isEmpty() -> {
                old_phone.setError("旧手机号码不能为空")
            }
            new_phone.text.toString().trim().isEmpty() -> {
                new_phone.setError("新手机号码不能为空")
            }
            else -> return true
        }
        return false
    }

    fun submit() {
        val map = mapOf(Pair("account_old", old_phone.text.toString())
                , Pair("account_new", new_phone.text.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                CustomDialog(message = "更换手机申请成功", positiveClicked = DialogInterface.OnClickListener { dialog, which ->
                    finish()
                })
            }

            override fun onError(context: Context, error: String) {
                CustomDialog("错误", message = error)
            }

            override fun onLoginErr(context: Context) {
                LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this@ChangePhoneActivity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(this, CACCOUNT.getInterface(), map)
    }
}