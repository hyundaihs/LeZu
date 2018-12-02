package com.cyf.union.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.adapters.LineDecoration
import com.cyf.lezu.entity.VipInfo
import com.cyf.lezu.entity.VipListRes
import com.cyf.lezu.initActionBar
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.CustomDialog
import com.cyf.lezu.utils.LoginErrDialog
import com.cyf.union.AppUnion
import com.cyf.union.R
import com.cyf.union.UserID
import com.cyf.union.entity.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_faquan.*
import kotlinx.android.synthetic.main.layout_faquan_list_item.view.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/4/14/014.
 */
class FaQuanActivity : MyBaseActivity() {

    var price: Int = 0
    var type: Int = 1
    var data = ArrayList<VipInfo>()
    val adapter = MyAdapter(data)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faquan)
        faquan_edu.text = "请谨慎发券，年总额度1万元\n剩余额度：${AppUnion.edu}"
        price = intent.getIntExtra("price", 0)
        type = intent.getIntExtra("type", 1)
        initActionBar(this, "发送${price}元券", rightBtn = "发送", rightClick = {
            if (AppUnion.edu > 0) {
                if (checkData()) {
                    if (AppUnion.logier_id == UserID.WORKER) {
                        faSong(FF_YHQ_YG)
                    } else {
                        faSong(FF_YHQ)
                    }
                }
            } else {
                toast("额度已用完")
            }
        })
        initRecyclerView()
        if (AppUnion.logier_id == UserID.WORKER) {
            getHyList(ZT_ACCOUNT_LISTS_YG)
        } else {
            getHyList(ZT_ACCOUNT_LISTS)
        }
    }

    private fun faSong(inter: String) {
        val checkedInfo = adapter.getAllChecked()
        val checkIds = ArrayList<Int>()
        for (i in 0 until checkedInfo.size) {
            checkIds.add(checkedInfo[i].id)
        }
        val map = mapOf(Pair("type_id", type.toString()), Pair("num", faquan_count.text.toString()),
                Pair("price", price.toString()), Pair("yxq", faquan_count.text.toString()), Pair("ids", Gson().toJson(checkIds)))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                CustomDialog(message = "发送成功", positiveClicked = android.content.DialogInterface.OnClickListener { dialog, which ->
                    finish()
                })
            }

            override fun onError(context: Context, error: String) {
                toast(error)
            }

            override fun onLoginErr(context: Context) {
                LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this@FaQuanActivity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(this, inter.getInterface(), map)
    }

    private fun checkData(): Boolean {
        val message = if (faquan_days.text.trim().isEmpty()) {
            "请输入有效期"
        } else if (faquan_count.text.trim().isEmpty()) {
            "请输入数量"
        } else if (!adapter.hasChecked()) {
            "请选择用户"
        } else {
            return true
        }
        CustomDialog(title = "错误", message = message)
        return false
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        faquan_RecyclerView.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        faquan_RecyclerView.adapter = adapter
        faquan_RecyclerView.addItemDecoration(LineDecoration(this, LineDecoration.VERTICAL))
        faquan_RecyclerView.itemAnimator = DefaultItemAnimator()
        faquan_RecyclerView.isNestedScrollingEnabled = false
    }

    private fun getHyList(inter: String) {
        val map = mapOf(Pair("page", "1"), Pair("pagesize", "100"))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val vipListRes = Gson().fromJson(result, VipListRes::class.java)
                data.clear()
                data.addAll(vipListRes.retRes)
                adapter.setCheckAll()
                adapter.notifyDataSetChanged()
            }

            override fun onError(context: Context, error: String) {
                toast(error)
            }

            override fun onLoginErr(context: Context) {
                LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(this@FaQuanActivity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(this, inter.getInterface(), map)
    }

    class MyAdapter(val data: ArrayList<VipInfo>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        private val checks = ArrayList<VipInfo>()

        init {
            setCheckAll()
        }

        fun hasChecked(): Boolean {
            return checks.size > 0
        }

        fun getAllChecked(): ArrayList<VipInfo> {
            return checks
        }

        fun setCheckAll() {
            checks.clear()
            checks.addAll(data)
        }

        private fun addCheck(vipInfo: VipInfo) {
            if (!checks.contains(vipInfo)) {
                checks.add(vipInfo)
            }
        }

        private fun deleteCheck(vipInfo: VipInfo) {
            if (checks.contains(vipInfo)) {
                checks.remove(vipInfo)
            }
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val vipInfo = data[position]
            holder.itemView.faquan_list_item_name.text = "用户姓名:${vipInfo.title}"
            holder.itemView.faquan_list_item_phone.text = "用户手机:${vipInfo.account}"
            holder.itemView.faquan_list_item_check.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    addCheck(vipInfo)
                } else {
                    deleteCheck(vipInfo)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_faquan_list_item, parent, false))
        }

        override fun getItemCount(): Int = data.size

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}