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
import kotlinx.android.synthetic.main.activity_forgetpsd.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/5/6/006.
 */
class ForgetPsdActivity : MyBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgetpsd)
        initActionBar(this, "忘记密码")
        getVerfCode.setOnClickListener {
            getCode()
        }
        forget_submit.setOnClickListener {
            if (check(false)) {
                submit()
            }
        }
    }

    fun check(isPhone: Boolean): Boolean {
        if (isPhone) {
            return !forget_account.text.toString().trim().isEmpty()
        } else {
            when {
                forget_account.text.toString().trim().isEmpty() -> {
                    forget_account.setError("手机号码不能为空")
                }
                forget_verfcode.text.toString().trim().isEmpty() -> {
                    forget_verfcode.setError("验证码不能为空")
                }
                forget_password.text.toString().trim().isEmpty() -> {
                    forget_password.setError("新密码不能为空")
                }
                else -> return true
            }
            return false
        }
    }

    private fun getCode() {
        if (check(true)) {
            val dialog = MyProgressDialog(this)
            val map = mapOf(Pair("phone", forget_account.text.toString()))
            MySimpleRequest(object : MySimpleRequest.RequestCallBack {
                override fun onSuccess(result: String) {
                    dialog.dismiss()
                }

                override fun onError(error: String) {
                    dialog.dismiss()
                    toast(error)
                    CustomDialog("错误", message = error)
                }

                override fun onLoginErr() {
                    dialog.dismiss()
                    LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                        val intent = Intent(this@ForgetPsdActivity, LoginActivity::class.java)
                        startActivity(intent)
                    })
                }

            }).postRequest(this as Context, MySimpleRequest.SENDMSG, map)
        }
    }

    fun submit() {
        val dialog = MyProgressDialog(this)
        val map = mapOf(Pair("account", forget_account.text.toString())
                , Pair("password", forget_password.text.toString())
                , Pair("verf", forget_verfcode.text.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(result: String) {
                dialog.dismiss()
                CustomDialog(message = "新密码修改成功", positiveClicked = android.content.DialogInterface.OnClickListener { dialog, which ->
                    finish()
                })
            }

            override fun onError(error: String) {
                dialog.dismiss()
                toast(error)
                CustomDialog("错误", message = error)
            }

            override fun onLoginErr() {
                dialog.dismiss()
                LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this@ForgetPsdActivity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(this as Context, MySimpleRequest.FPASS, map)
    }


}