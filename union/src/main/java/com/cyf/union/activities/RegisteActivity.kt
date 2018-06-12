package com.cyf.union.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.initActionBar
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.CustomDialog
import com.cyf.lezu.utils.LoginErrDialog
import com.cyf.lezu.utils.MyProgressDialog
import com.cyf.union.R
import kotlinx.android.synthetic.main.activity_forgetpsd.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registe.*
import com.uuzuche.lib_zxing.activity.CaptureActivity
import com.uuzuche.lib_zxing.activity.CodeUtils


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/5/6/006.
 */
class RegisteActivity : MyBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registe)
        initActionBar(this, "注册")
        init()
    }

    private fun init() {
        registe_getVerfCode.setOnClickListener { onClick(it) }
        registe_scanCode.setOnClickListener { onClick(it) }
        registe_submit.setOnClickListener { onClick(it) }
        registe_back.setOnClickListener { onClick(it) }
    }

    private fun onClick(v: View) {
        when (v.id) {
            R.id.registe_getVerfCode -> getVerfCode()
            R.id.registe_scanCode -> scanCode()
            R.id.registe_submit -> submit()
            R.id.registe_back -> finish()
        }
    }

    private fun check(isPhone: Boolean): Boolean {
        if (isPhone) {
            return !registe_account.text.toString().trim().isEmpty()
        } else {
            when {
                registe_account.text.toString().trim().isEmpty() -> {
                    registe_account.setError("手机号码不能为空")
                }
                registe_verfcode.toString().trim().isEmpty() -> {
                    registe_verfcode.setError("验证码不能为空")
                }
                registe_store.toString().trim().isEmpty() -> {
                    registe_store.setError("店铺ID不能为空")
                }
                registe_name.toString().trim().isEmpty() -> {
                    registe_name.setError("员工名称不能为空")
                }
                else -> return true
            }
            return false
        }
    }

    private fun getVerfCode() {
        if (check(true)) {
            val dialog = MyProgressDialog(this)
            val map = mapOf(Pair("phone", registe_account.text.toString()))
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
                        val intent = Intent(this@RegisteActivity, LoginActivity::class.java)
                        startActivity(intent)
                    })
                }

            }).postRequest(this as Context, MySimpleRequest.SENDMSG, map)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 11) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                val bundle = data.getExtras()
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    val result = bundle.getString(CodeUtils.RESULT_STRING)
                    registe_store.setText(result)
                    toast("解析二维码成功${result}")
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    toast("解析二维码失败")
                } else {
                    toast("else")
                }
            } else {
                toast("data is null")
            }
        } else {
            toast("code is not 11")
        }
    }

    private fun scanCode() {
        val intent = Intent(this@RegisteActivity, CaptureActivity::class.java)
        startActivityForResult(intent, 11)
    }

    private fun submit() {
        if (check(false)) {
            val dialog = MyProgressDialog(this)
            val map = mapOf(Pair("title", registe_account.text.toString())
                    , Pair("account", registe_account.text.toString())
                    , Pair("verf", registe_verfcode.text.toString())
                    , Pair("zt_id", registe_store.text.toString()))
            MySimpleRequest(object : MySimpleRequest.RequestCallBack {
                override fun onSuccess(result: String) {
                    dialog.dismiss()
                    CustomDialog(message = "注册成功", positiveClicked = DialogInterface.OnClickListener { dialog, which ->
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
                        val intent = Intent(this@RegisteActivity, LoginActivity::class.java)
                        startActivity(intent)
                    })
                }

            }).postRequest(this as Context, MySimpleRequest.REG, map)
        }
    }


}