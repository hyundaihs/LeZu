package com.cyf.union.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.entity.LoginInfoRes
import com.cyf.lezu.initActionBar
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.requests.MySimpleRequest.Companion.LOGIN
import com.cyf.lezu.requests.MySimpleRequest.Companion.LOGINYG
import com.cyf.lezu.toast
import com.cyf.lezu.utils.PreferenceUtil
import com.cyf.lezu.utils.PreferenceUtil.Companion.ACCOUNT
import com.cyf.lezu.utils.PreferenceUtil.Companion.LOGIN_VERF
import com.cyf.lezu.utils.PreferenceUtil.Companion.PASSWORD
import com.cyf.union.AppUnion
import com.cyf.union.R
import com.cyf.union.UserID
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*

/**
 * LeZu
 * Created by ${蔡雨峰} on 2018/1/6/006.
 */
class LoginActivity : MyBaseActivity() {

    var login_verf: String by PreferenceUtil(AppUnion.instance, LOGIN_VERF, "")
    var m_Account: String by PreferenceUtil(AppUnion.instance, ACCOUNT, "")
    var m_Password: String by PreferenceUtil(AppUnion.instance, PASSWORD, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loadLayout.isEnabled = false
        initActionBar(this, "登录")
        initViews()
    }

    private fun initViews() {
        bossSignBtn.setOnClickListener { onClick(it) }
        workSignBtn.setOnClickListener { onClick(it) }
        forgetPsdBtn.setOnClickListener { onClick(it) }
        helpInfo.setOnClickListener { onClick(it) }
        boss_check_account.setText(m_Account)
        password.setText(m_Password)
    }

    private fun onClick(v: View) {
        when (v.id) {
            R.id.bossSignBtn -> actionLogin(true)
            R.id.workSignBtn -> actionLogin(false)
            R.id.forgetPsdBtn -> forgetPsd()
            R.id.helpInfo -> regist()
        }
    }

    private fun forgetPsd() {
        startActivity(Intent(this@LoginActivity, ForgetPsdActivity::class.java))
    }

    private fun regist() {
        startActivity(Intent(this@LoginActivity, RegisteActivity::class.java))
    }

    private fun actionLogin(isBoss: Boolean) {
        loadLayout.isRefreshing = true
        var inter = LOGIN
        if (isBoss) {
            AppUnion.logier_id = UserID.BOSS
            inter = LOGIN
        } else {
            AppUnion.logier_id = UserID.WORKER
            inter = LOGINYG
        }

        val map = mapOf(Pair("account", boss_check_account.text.toString()), Pair("password", password.text.toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onLoginErr() {
                loadLayout.isRefreshing = false
            }

            override fun onSuccess(result: String) {
                loadLayout.isRefreshing = false
                val loginInfoRes = Gson().fromJson(result, LoginInfoRes::class.java)
                login_verf = loginInfoRes.retRes.login_verf
                m_Account = boss_check_account.text.toString()
                m_Password = password.text.toString()
                AppUnion.isLogged = true
                toast("登陆成功")
                gotoHome()
            }

            override fun onError(error: String) {
                loadLayout.isRefreshing = false
                toast("登陆失败" + error)
            }

        }).postRequest(this, inter, map)
    }

    private fun gotoHome() {
        if (AppUnion.isLogged) {
            val intent = Intent()
            intent.setClass(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}