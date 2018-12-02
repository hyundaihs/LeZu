package com.cyf.union.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.cyf.lezu.D
import com.cyf.lezu.entity.CheckInfo
import com.cyf.lezu.entity.CheckInfoRes
import com.cyf.lezu.fragments.BaseFragment
import com.cyf.lezu.initActionBar
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.CustomDialog
import com.cyf.lezu.utils.LoginErrDialog
import com.cyf.lezu.utils.MyProgressDialog
import com.cyf.union.AppUnion.Companion.workerList
import com.cyf.union.activities.LoginActivity
import com.cyf.union.R
import com.cyf.union.entity.FFJX
import com.cyf.union.entity.QDXQ
import com.cyf.union.entity.XGJX
import com.cyf.union.entity.getInterface
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_boss_check.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/1/6/006.
 * Boss考勤查询
 */
class BossQueryCheckFragment : BaseFragment() {
    var textViews = ArrayList<TextView>()
    var isFirst = false

    companion object {
        var selectWorker = 0
        var selectWeek = 0;
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_boss_check, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.initActionBar(activity as AppCompatActivity, "考勤", false)
        initRecyclerView()
    }

    private fun getCheckInfo() {
        if (!isFirst) {
            isFirst = true
            return
        }
        val map = mapOf(Pair("yg_id", workerList[selectWorker].id.toString()), Pair("week_num", (selectWeek + 1).toString()))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val checkInfoRes = Gson().fromJson(result, CheckInfoRes::class.java)
                val checkInfo = checkInfoRes.retRes
                initViews(checkInfo)
            }

            override fun onError(context: Context, error: String) {
                activity?.toast(error)
            }

            override fun onLoginErr(context: Context) {
                activity?.LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(activity as Context, QDXQ.getInterface(), map)
    }

    private fun initViews(checkInfo: CheckInfo) {
        D(checkInfo.toString())
        boss_check_account.text = checkInfo.info.account
        boss_check_number.text = checkInfo.info.orders_num.toString()
        boss_check_price.text = "¥${checkInfo.info.orders_price}"
        boss_check_qdl.text = "月签到率:${checkInfo.info.qdl}%"
        textViews.add(week_7)
        textViews.add(week_1)
        textViews.add(week_2)
        textViews.add(week_3)
        textViews.add(week_4)
        textViews.add(week_5)
        textViews.add(week_6)
        for (i in 0 until 7) {
            setCheck(textViews[i], false)
        }
        for (i in 0 until checkInfo.lists.size) {
            if (checkInfo.lists[i].week >= 0) {
                setCheck(textViews[checkInfo.lists[i].week], true)
            }
        }
    }

    private fun setCheck(view: TextView, isChecked: Boolean) {
        if (isChecked) {
            view.setBackgroundColor(resources.getColor(R.color.faquan_text_color))
        } else {
            view.setBackgroundColor(resources.getColor(R.color.faquan_text_bg))
        }
    }

    private fun initRecyclerView() {
        val data = ArrayList<String>()
        for (i in 0 until workerList.size) {
            data.add(workerList[i].title)
        }
        val arrayAdapter = ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, data)
        worker_spinner.adapter = arrayAdapter
        worker_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectWorker = 0
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectWorker = position
                getCheckInfo()
            }

        }
        week_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectWeek = 0
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectWeek = position
                getCheckInfo()
            }

        }
    }


}

