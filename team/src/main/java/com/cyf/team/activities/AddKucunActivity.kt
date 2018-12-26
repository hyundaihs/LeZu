package com.cyf.team.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.view.View
import com.cyf.lezu.D
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.adapters.LineDecoration
import com.cyf.lezu.adapters.MyBaseAdapter
import com.cyf.lezu.initActionBar
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.CustomDialog
import com.cyf.lezu.utils.LoginErrDialog
import com.cyf.team.AppTeam
import com.cyf.team.OrderListType
import com.cyf.team.R
import com.cyf.team.entity.*
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_kucun.*
import kotlinx.android.synthetic.main.fragment_orders.*
import kotlinx.android.synthetic.main.layout_chooser_item.view.*
import kotlinx.android.synthetic.main.layout_kucun_item.view.*
import kotlinx.android.synthetic.main.layout_orders_item_cargos.view.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/22/022.
 */
class AddKucunActivity : MyBaseActivity() {

    var isCK = false
    var type = ""
    var cargoNums = ArrayList<CargoNum>()
    var fileUrl = ""
    lateinit var cargoDetails: CargoDetails
    val chooserNums = ArrayList<CargoNum>()
    private val chooserAdapter = ChooserAdapter(chooserNums)
    var currindex = 0
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_kucun)
        initActionBar(this, "出库设备", rightBtn = "确定", rightClick = {
            var isOk = true
            for (i in 0 until cargoNums.size) {
                if (cargoNums[i].numbers == "") {
                    isOk = false
                }
            }
            if (isOk) {
                CustomDialog("提示", if (isCK) "确定要出库吗？" else "确定要入库吗？", positiveClicked = DialogInterface.OnClickListener { _, _ ->
                    submit()
                }, negative = "取消")
            } else {
                toast("请选择编号")
            }
        })
        isCK = intent.getBooleanExtra("isCK", true)
        type = intent.getStringExtra("type")
        id = intent.getIntExtra("id", 0)
        cargoDetails = AppTeam.cargoDetails
        initViews()
    }

    private fun initViews() {
        if (!isCK) {
            addKCContents.isEnabled = false
        }

        fileUrl = cargoDetails.file_url
        addKCName.text = if (cargoDetails.goods_title == null || cargoDetails.goods_title.isEmpty()) cargoDetails.title else cargoDetails.goods_title
        addKCColor.text = cargoDetails.yanse
        addKCType.text = cargoDetails.guige
        addKCModel.text = cargoDetails.xinghao
        addKCNum.text = cargoDetails.num.toString()
        if (isCK) {
            for (i in 0 until cargoDetails.num) {
                cargoNums.add(CargoNum("", "0", type, id.toString(), cargoDetails.id.toString(), "100"))
            }
        }
        val adapter = MyAdapter(cargoNums)
        val layoutManager1 = LinearLayoutManager(this)
        addKCList.layoutManager = layoutManager1
        layoutManager1.orientation = OrientationHelper.VERTICAL
        addKCList.addItemDecoration(LineDecoration(this, LineDecoration.VERTICAL))
        addKCList.itemAnimator = DefaultItemAnimator()
        addKCList.isNestedScrollingEnabled = false
        addKCList.adapter = adapter

        val layoutManager2 = LinearLayoutManager(this)
        addKCNumList.layoutManager = layoutManager2
        layoutManager2.orientation = OrientationHelper.VERTICAL
        addKCNumList.addItemDecoration(LineDecoration(this, LineDecoration.VERTICAL))
        addKCNumList.itemAnimator = DefaultItemAnimator()
        addKCNumList.isNestedScrollingEnabled = false
        addKCNumList.adapter = chooserAdapter
        chooserAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                cargoNums[currindex].numbers = chooserNums[position].numbers
                cargoNums[currindex].xinjiu = chooserNums[position].xinjiu
                cargoNums[currindex].num = if (isCK) "-1" else "1"
                cargoNums[currindex].orders_type = type
                adapter.notifyDataSetChanged()
                addKCNumList.visibility = View.GONE
            }
        }
    }

    private inner class MyAdapter(val data: ArrayList<CargoNum>) : MyBaseAdapter(R.layout.layout_kucun_item) {
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val picasso = Picasso.with(holder.itemView.context)
            picasso.load(IMAGE_URL + fileUrl).into(holder.itemView.kcImage)
            holder.itemView.kcNumbers.setText(data[position].numbers)
            holder.itemView.kcXinJ.setText(data[position].xinjiu)
            holder.itemView.kcCheckNum.setOnClickListener {
                //搜索库存编号
                currindex = position
                getNums()
            }
            holder.itemView.kcCaigou.setOnClickListener {
                it.context.toast("申请采购流程暂未启动")
            }
            if (!isCK) {
                holder.itemView.kcNumbers.isEnabled = false
                holder.itemView.kcXinJ.isEnabled = false
                holder.itemView.kcCheckNum.visibility = View.GONE
                holder.itemView.kcCaigou.visibility = View.GONE
            }
        }

        override fun getItemCount(): Int = data.size
    }

    private inner class ChooserAdapter(val data: ArrayList<CargoNum>) : MyBaseAdapter(R.layout.layout_chooser_item) {
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            holder.itemView.chooserText.text = data[position].numbers
        }

        override fun getItemCount(): Int = data.size
    }

    private fun getNums() {
        val map = mapOf(
                Pair("guige", cargoDetails.guige)
                , Pair("xinghao", cargoDetails.xinghao)
                , Pair("yanse", cargoDetails.yanse)
                , Pair("type_id", if (isCK) "1" else "2")
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val cargoDetailRes = Gson().fromJson(result, CargoDetailRes::class.java)
                chooserNums.clear()
                chooserNums.addAll(cargoDetailRes.retRes.numbers_lists)
                chooserAdapter.notifyDataSetChanged()
                addKCNumList.visibility = View.VISIBLE
            }

            override fun onError(context: Context, error: String) {
                toast(error)
            }

            override fun onLoginErr(context: Context) {
                LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, true).postRequest(this as Context, "kucuninfo".getInterface(), map)
    }

    private fun submit() {
        val map = mapOf(
                Pair("kucunguige_id", cargoDetails.kucunguige_id)
                , Pair("contents", addKCContents.text.toString())
                , Pair("numbers_arr", cargoNums)
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                toast("操作成功")
                finish()
            }

            override fun onError(context: Context, error: String) {
                toast(error)
            }

            override fun onLoginErr(context: Context) {
                LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }, true).postRequest(this as Context, "rukucuku".getInterface(), map)
    }
}