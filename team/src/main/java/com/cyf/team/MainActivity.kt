package com.cyf.team

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cyf.lezu.requestPermission
import com.cyf.lezu.toast
import com.cyf.lezu.utils.AppPath
import com.cyf.lezu.utils.CustomDialog
import com.cyf.team.activities.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission(onDenied = {
            finish()
        })
        AppPath(this)
        setContentView(R.layout.activity_main)
        startWork.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        introduce.setOnClickListener {
            ////            val intent = Intent(this, WebActivity::class.java)
////            intent.putExtra("type", 2)
////            intent.putExtra("html", LeZuInfoUrl)
////            intent.putExtra("pageName", "平台介绍")
////            startActivity(intent)
        }
        newsInfo.setOnClickListener {
            ////            val intent = Intent(this, WebActivity::class.java)
////            intent.putExtra("type", 2)
////            intent.putExtra("html", LeZuInfoUrl)
////            intent.putExtra("pageName", "新闻资讯")
////            startActivity(intent)
        }
        version.setOnClickListener {
            checkVersion()
        }
    }

    private fun checkVersion() {
        val version = VersionUtil(this)
        version.check(object : VersionUtil.VersionCallBack {
            override fun hasNewVersion(versionName: String, versionCode: Int, url: String, oldVerName: String, oldVerCode: Int) {
                CustomDialog("发现新版本", "是否更新？\n当前版本为：${oldVerName}\n最新版本为：${versionName}"
                        , "立即更新", android.content.DialogInterface.OnClickListener { dialog, which ->
                    toast("正在后台下载...")
                    version.downLoadFile(
                            url,
                            "${AppPath.APK}LeZu_${versionName}.apk",
                            object : VersionUtil.ReqProgressCallBack {
                                override fun onProgress(total: Long, current: Long) {

                                }

                                override fun onSuccess(file: File) {
                                    version.installApk(file)
                                }

                                override fun onFailed(e: String) {
                                    toast("新版本下载失败")
                                }
                            }
                    )
                }, "暂不更新")
            }

            override fun noNewVersion(versionName: String, versionCode: Int) {
                CustomDialog("已是最新版本", "当前版本为：${versionName}")
            }

        })
    }

}
