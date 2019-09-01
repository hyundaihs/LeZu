package com.cyf.team.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import cn.jpush.android.api.JPushInterface
import com.cyf.lezu.D
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.entity.LoginInfoRes
import com.cyf.lezu.initActionBar
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.PreferenceUtil
import com.cyf.lezu.utils.PreferenceUtil.Companion.ACCOUNT
import com.cyf.lezu.utils.PreferenceUtil.Companion.LOGIN_VERF
import com.cyf.lezu.utils.PreferenceUtil.Companion.PASSWORD
import com.cyf.team.AppTeam
import com.cyf.team.R
import com.cyf.team.UserID
import com.cyf.team.entity.LOGIN
import com.cyf.team.entity.getInterface
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/1/001.
 */
class LoginActivity : MyBaseActivity() {
    var login_verf: String by PreferenceUtil(AppTeam.instance, LOGIN_VERF, "")
    var m_Account: String by PreferenceUtil(AppTeam.instance, ACCOUNT, "")
    var m_Password: String by PreferenceUtil(AppTeam.instance, PASSWORD, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loadLayout.isEnabled = false
        initActionBar(this, "登录")
        initViews()
    }

    private fun initViews() {
        login_KGBtn.setOnClickListener { onClick(it) }
        login_XJBtn.setOnClickListener { onClick(it) }
        login_GRBtn.setOnClickListener { onClick(it) }
//        forgetPsdBtn.setOnClickListener { onClick(it) }
//        register.setOnClickListener { onClick(it) }
        login_account.setText(m_Account)
        login_password.setText(m_Password)
    }

    private fun onClick(v: View) {
        when (v.id) {
            R.id.login_KGBtn -> actionLogin(UserID.KU_GUAN)
            R.id.login_XJBtn -> actionLogin(UserID.XUN_JIAN)
            R.id.login_GRBtn -> actionLogin(UserID.GONG_REN)
//            R.id.forgetPsdBtn -> forgetPsd()
//            R.id.register -> regist()
        }
    }

    private fun changePhone() {
//        startActivity(Intent(this@LoginActivity, ChangePhoneActivity::class.java))
    }

    private fun forgetPsd() {
//        startActivity(Intent(this@LoginActivity, ForgetPsdActivity::class.java))
    }

    private fun regist() {
//        startActivity(Intent(this@LoginActivity, RegisteActivity::class.java))
    }

    private fun actionLogin(id: Int) {
        loadLayout.isRefreshing = true
        AppTeam.logier_id = id
        val map = mapOf(Pair("account", login_account.text.toString())
                , Pair("password", login_password.text.toString())
                , Pair("type_id", id.toString())
                ,Pair("jpush_id", AppTeam.jpush_id)
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                loadLayout.isRefreshing = false
                val loginInfoRes = Gson().fromJson(result, LoginInfoRes::class.java)
                login_verf = loginInfoRes.retRes.login_verf
                m_Account = login_account.text.toString()
                m_Password = login_password.text.toString()
                AppTeam.isLogged = true
                toast("登陆成功")
                gotoHome()
            }

            override fun onError(context: Context, error: String) {
                loadLayout.isRefreshing = false
                toast("登陆失败$error")
            }

            override fun onLoginErr(context: Context) {
                loadLayout.isRefreshing = false
            }

        }, false).postRequest(this, LOGIN.getInterface(), map)
    }

    private fun gotoHome() {
        if (AppTeam.isLogged) {
            val intent = Intent()
            intent.setClass(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}