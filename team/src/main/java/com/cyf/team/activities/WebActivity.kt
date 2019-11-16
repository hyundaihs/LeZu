package com.cyf.team.activities

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/1/001.
 */
import android.app.AlertDialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.webkit.*
import com.cyf.lezu.D
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.initActionBar
import com.cyf.lezu.sendToWx
import com.cyf.lezu.utils.BottomDialog
import com.cyf.lezu.utils.MyProgressDialog
import com.cyf.team.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_web.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/5/6/006.
 */
class WebActivity : MyBaseActivity() {
    var dialog: AlertDialog? = null
    var type: Int = 0
    var html: String = ""
    var title: String = ""
    var price1: Double = 0.0
    var price2: Double = 0.0
    var url: String = ""
    var pageName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        type = intent.getIntExtra("type", 0)
        html = intent.getStringExtra("html")
        pageName = intent.getStringExtra("pageName")
        if (type == 1) {//type  为1  在线加载
            title = intent.getStringExtra("title")
            url = intent.getStringExtra("url")
            price1 = intent.getDoubleExtra("price1", 0.0)
            price2 = intent.getDoubleExtra("price2", 0.0)
//            initActionBar(this, pageName, rightBtn = "分享", rightClick = {
//                share()
//            })
        } else {
            initActionBar(this, pageName)
        }
        initWebView(html)
    }

    private fun initWebView(url: String) {
        val webSettings = rule_content.getSettings()
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)
        webSettings.setJavaScriptEnabled(true)
        webSettings.setAllowContentAccess(true)
        webSettings.setAppCacheEnabled(false)
        webSettings.setBuiltInZoomControls(false)
        webSettings.setUseWideViewPort(true)
        webSettings.setLoadWithOverviewMode(true)
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN)

        webSettings.setDomStorageEnabled(true) //重点是这个设置
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
//        }
        //加载需要显示的网页
        if (type == 1) {
            rule_content.loadUrl(url)
        } else if (type == 2) {
            rule_content.loadUrl(url)
        } else {
            rule_content.loadData(url, "text/html charset=UTF-8", null)
        }
        //设置Web视图
        rule_content.webViewClient = webViewClient()

        rule_content.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (newProgress == 100) {
                    dialog?.dismiss()
                } else {
                    if (null == dialog) {
                        dialog = MyProgressDialog(view.context)
                    }
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && rule_content.canGoBack()) {
            rule_content.goBack() //goBack()表示返回WebView的上一页面
            return true
        }
        finish()//结束退出程序
        return false
    }

    //Web视图
    private inner class webViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: String): Boolean {
            view.loadUrl(request)
            return true
        }
    }

//    private fun share() {
//        val view = LayoutInflater.from(this).inflate(R.layout.layout_wx_share, null, false)
//        val dialog = this.BottomDialog(view)
//        val picasso = Picasso.with(this)
//        view.pyq.setOnClickListener {
//            dialog.dismiss()
//            val pDialog = MyProgressDialog(this)
//            doAsync {
//                val bitmap = picasso.load(IMAGE_URL + url).get()
//                uiThread {
//                    val rel = AppUnion.instance.api.sendToWx(title, price1, price2, html, bmp = bitmap, isTimeLine = true)
//                    pDialog.dismiss()
//                    D("发送${if (rel) "成功" else "失败"}")
//                }
//            }
//        }
//        view.wxhy.setOnClickListener {
//            dialog.dismiss()
//            val pDialog = MyProgressDialog(this)
//            doAsync {
//                val bitmap = picasso.load(IMAGE_URL + url).get()
//                uiThread {
//                    val rel = AppUnion.instance.api.sendToWx(title, price1, price2, html, bmp = bitmap, isTimeLine = false)
//                    D("发送${if (rel) "成功" else "失败"}")
//                    pDialog.dismiss()
//                }
//            }
//        }
//        view.ewm.setOnClickListener {
//            dialog.dismiss()
//            if (null != WorkSalePlatFragment.workerDetails)
//                this.ShowImageDialog(WorkSalePlatFragment.workerDetails!!.ewm_file_url)
//        }
//    }
}