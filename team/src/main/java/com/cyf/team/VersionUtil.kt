package com.cyf.team

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import android.text.TextUtils
import com.cyf.lezu.D
import com.cyf.lezu.E
import com.cyf.lezu.entity.VersionInfo
import com.cyf.lezu.entity.VersionInfoRes
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.team.entity.CV
import com.cyf.team.entity.getInterface
import com.google.gson.Gson
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/6/16/016.
 */
class VersionUtil(val context: Activity) {

    private val mOkHttpClient: OkHttpClient by lazy {
        OkHttpClient()
    }
    val localVersionCode by lazy {
        context.packageManager.getPackageInfo(context.packageName, 0).versionCode
    }
    val localVersionName by lazy {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }

    fun check(versionCallBack: VersionCallBack) {
        getServiceVersion(versionCallBack)
    }

    private fun getServiceVersion(versionCallBack: VersionCallBack) {
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val versionInfoRes = Gson().fromJson(result, VersionInfoRes::class.java)
                val versionInfo = versionInfoRes.retRes
                match(versionInfo, versionCallBack)
            }

            override fun onError(context: Context, error: String) {
                versionCallBack.noNewVersion(localVersionName, localVersionCode)
            }

            override fun onLoginErr(context: Context) {
            }

        }, false).postRequest(context, CV.getInterface(), mapOf(Pair("app", "android")))
    }

    private fun match(versionInfo: VersionInfo, versionCallBack: VersionCallBack) {
        if (localVersionCode < versionInfo.v_num) {
            versionCallBack.hasNewVersion(versionInfo.v_title, versionInfo.v_num, versionInfo.http_url, localVersionName, localVersionCode)
        } else {
            versionCallBack.noNewVersion(localVersionName, localVersionCode)
        }
    }

    interface VersionCallBack {
        fun hasNewVersion(versionName: String, versionCode: Int, url: String, oldVerName: String, oldVerCode: Int)

        fun noNewVersion(versionName: String, versionCode: Int)
    }

    /**
     * 下载文件
     * @param fileUrl 文件url
     * @param destFileDir 存储目标目录带文件名
     */
    fun downLoadFile(fileUrl: String, destFileDir: String, callBack: ReqProgressCallBack) {
        context.doAsync {
            D("下载文件${fileUrl}到$destFileDir")
            val file = File(destFileDir)
            if (file.exists()) {
                file.delete()
            }
            val request = Request.Builder().url(fileUrl).build()
            val call = mOkHttpClient.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    E(e.toString())
                    uiThread {
                        callBack.onFailed(e.toString())
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    var ips: InputStream? = null
                    val buf = ByteArray(2048)
                    var len = 0
                    var fos: FileOutputStream? = null
                    try {
                        val total = response.body()!!.contentLength()
                        var current: Long = 0
                        ips = response.body()!!.byteStream()
                        fos = FileOutputStream(file)
                        if (ips == null) {
                            uiThread {
                                callBack.onFailed("数据获取失败")
                            }
                            return
                        }
                        while (true) {
                            len = ips.read(buf)
                            if (len == -1) {
                                break
                            }
                            current += len.toLong()
                            fos.write(buf, 0, len)
                            uiThread {
                                callBack.onProgress(total, current)
                            }
                        }
                        fos.flush()
                        uiThread {
                            callBack.onSuccess(file)
                        }
                    } catch (e: IOException) {
                        E(e.toString())
                        uiThread {
                            callBack.onFailed(e.toString())
                        }
                    } finally {
                        try {
                            if (ips != null) {
                                ips.close()
                            }
                            if (fos != null) {
                                fos.close()
                            }
                        } catch (e: IOException) {
                            E(e.toString())
                        }

                    }
                }

            })
        }
    }

    interface ReqProgressCallBack {
        fun onProgress(total: Long, current: Long)
        fun onSuccess(file: File)
        fun onFailed(e: String)
    }

    fun installApk(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)

        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            //provider authorities
            val apkUri = FileProvider.getUriForFile(context, "com.lezu.fileprovider", file)
            //Granting Temporary Permissions to a URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }

        context.startActivity(intent)
    }

    fun installApk(apkPath: String) {
        if (TextUtils.isEmpty(apkPath)) {
            return
        }
        val file = File(apkPath)
        installApk(file)
    }
}