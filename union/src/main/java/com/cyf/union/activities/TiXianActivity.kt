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
import kotlinx.android.synthetic.main.activity_ti_xian.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/4/30/030.
 */
class TiXianActivity : MyBaseActivity() {

    var yue: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ti_xian)
        initActionBar(this, "提现")
        yue = intent.getDoubleExtra("price", 0.0)
        ti_xian_yue.text = "$yue"
        ti_xian_submit.setOnClickListener {
            if (checkEdits()) {
                submit()
            }
        }
    }

    private fun checkEdits(): Boolean {
        when {
            yue <= 0 -> toast("余额不足")
            ti_xian_price.text.toString().trim().isEmpty() -> ti_xian_price.error = "提现金额不能为空"
            ti_xian_name.text.toString().trim().isEmpty() -> ti_xian_name.error = "持卡人姓名不能为空"
            ti_xian_bank_name.text.toString().trim().isEmpty() -> ti_xian_bank_name.error = "银行名称不能为空"
            ti_xian_bank_num.text.toString().trim().isEmpty() -> ti_xian_bank_num.error = "银行卡号不能为空"
            ti_xian_phone.text.toString().trim().isEmpty() -> ti_xian_phone.error = "手机号码不能为空"
            ti_xian_price.text.toString().toDoubleOrNull() == null -> ti_xian_price.error = "提现金额必须为数字"
            ti_xian_price.text.toString().toDouble() > yue -> ti_xian_price.error = "提现金额不能大于余额"
            else -> return true
        }
        return false
    }

    private fun submit() {
        val dialog = MyProgressDialog(this)
        val map = mapOf(Pair("bank_name", ti_xian_bank_name.text.toString()),
                Pair("card_numbers", ti_xian_bank_num.text.toString()),
                Pair("price", ti_xian_price.text.toString()),
                Pair("title", ti_xian_name.text.toString()),
                Pair("phone", ti_xian_phone.text.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(result: String) {
                dialog.dismiss()
                CustomDialog(message = "提现申请成功", positiveClicked = DialogInterface.OnClickListener { _, _ ->
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
                    val intent = Intent(this@TiXianActivity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(this as Context, MySimpleRequest.TXSQ, map)
    }
}