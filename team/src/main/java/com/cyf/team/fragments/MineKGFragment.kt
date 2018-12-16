package com.cyf.team.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.cyf.lezu.adapters.LineDecoration
import com.cyf.lezu.adapters.MyBaseAdapter
import com.cyf.lezu.entity.WorkerListRes
import com.cyf.lezu.fragments.BaseFragment
import com.cyf.lezu.initActionBar
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.CalendarUtil
import com.cyf.lezu.utils.LoginErrDialog
import com.cyf.lezu.utils.ToastUtil
import com.cyf.lezu.widget.SwipeRefreshAndLoadLayout
import com.cyf.team.R
import com.cyf.team.activities.LoginActivity
import com.cyf.team.entity.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_mine_kg.*
import kotlinx.android.synthetic.main.layout_cargos_item.view.*
import kotlinx.android.synthetic.main.layout_cargos_item_item.view.*
import kotlinx.android.synthetic.main.layout_cargos_item_item_item.view.*
import kotlinx.android.synthetic.main.layout_cargos_spinner.view.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/1/001.
 */
class MineKGFragment : BaseFragment() {

    private val cargoTypes = ArrayList<CargoType>()
    private val cargoKucuns = ArrayList<CargoKucun>()
    private val sAdapter = MySpinnerAdapter(cargoTypes)
    private val mAdapter = MyAdapter(cargoKucuns)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mine_kg, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViews()
    }

    override fun onResume() {
        super.onResume()
        activity?.initActionBar(activity as AppCompatActivity, "资产盘点", false, rightBtn = "类型", rightClick = {
            if (cargosTypes.visibility == View.VISIBLE) {
                cargosTypes.visibility = View.GONE
            } else {
                cargosTypes.visibility = View.VISIBLE
            }
        })
    }

    private fun initViews() {
        val layoutManager1 = LinearLayoutManager(context)
        cargosTypes.layoutManager = layoutManager1
        layoutManager1.orientation = OrientationHelper.VERTICAL
        cargosTypes.addItemDecoration(LineDecoration(context, LineDecoration.VERTICAL))
        cargosTypes.itemAnimator = DefaultItemAnimator()
        cargosTypes.isNestedScrollingEnabled = false
        cargosTypes.adapter = sAdapter

        val layoutManager = LinearLayoutManager(context)
        cargosList.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        cargosList.itemAnimator = DefaultItemAnimator()
        cargosList.isNestedScrollingEnabled = false
        cargosList.adapter = mAdapter
        getCargoTypeList()
        getCargoList(-1)

        sAdapter.myOnItemClickListener = object : MyBaseAdapter.MyOnItemClickListener {
            override fun onItemClick(parent: MyBaseAdapter, view: View, position: Int) {
                getCargoList(cargoTypes[position].id)
                cargosTypes.visibility = View.GONE
            }
        }
        cargosList.setOnTouchListener { v, event ->
            cargosTypes.visibility = View.GONE
            false
        }
    }

    private inner class MySpinnerAdapter(val data: ArrayList<CargoType>) : MyBaseAdapter(R.layout.layout_cargos_spinner) {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val cargoType = data[position]
            holder.itemView.type.text = cargoType.title
        }

        override fun getItemCount(): Int = data.size
    }

    private inner class MyAdapter(val data: ArrayList<CargoKucun>) : MyBaseAdapter(R.layout.layout_cargos_item) {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val cargoKucun = data[position]
            holder.itemView.cargosName.text = cargoKucun.title
            val layoutManager = LinearLayoutManager(context)
            holder.itemView.cargosItemList.layoutManager = layoutManager
            layoutManager.orientation = OrientationHelper.VERTICAL
            holder.itemView.cargosItemList.itemAnimator = DefaultItemAnimator()
            holder.itemView.cargosItemList.isNestedScrollingEnabled = false
            holder.itemView.cargosItemList.adapter = MyAdapter2(cargoKucun.lists)
        }

        override fun getItemCount(): Int = data.size
    }

    private inner class MyAdapter2(val data: ArrayList<CargoDetail>) : MyBaseAdapter(R.layout.layout_cargos_item_item) {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val cargoDetail = data[position]
            holder.itemView.cargosModel.text = cargoDetail.xinghao
            holder.itemView.cargosColor.text = cargoDetail.yanse
            holder.itemView.cargosType.text = cargoDetail.title
            holder.itemView.cargosNum.text = cargoDetail.num.toString()
            holder.itemView.cargosPos.text = position.toString()
            if (cargoDetail.numbers_lists.size > 0) {
                holder.itemView.layoutBaozhi.visibility = View.VISIBLE
                val layoutManager = LinearLayoutManager(context)
                holder.itemView.cargoCargos.layoutManager = layoutManager
                layoutManager.orientation = OrientationHelper.VERTICAL
                holder.itemView.cargoCargos.itemAnimator = DefaultItemAnimator()
                holder.itemView.cargoCargos.isNestedScrollingEnabled = false
                holder.itemView.cargoCargos.adapter = MyAdapter3(cargoDetail.numbers_lists)
            } else {
                holder.itemView.layoutBaozhi.visibility = View.GONE
            }
        }

        override fun getItemCount(): Int = data.size
    }

    private inner class MyAdapter3(val data: ArrayList<CargoNum>) : MyBaseAdapter(R.layout.layout_cargos_item_item_item) {

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            super.onBindViewHolder(holder, position)
            val cargoNum = data[position]
            holder.itemView.item_num.text = cargoNum.numbers
            holder.itemView.item_baozhi.text = "${cargoNum.xinjiu}%"
            if (position % 2 == 0) {
                holder.itemView.item_num.setBackgroundResource(R.color.bg_F0F0F0)
                holder.itemView.item_baozhi.setBackgroundResource(R.color.bg_F0F0F0)
            } else {
                holder.itemView.item_num.setBackgroundColor(Color.WHITE)
                holder.itemView.item_baozhi.setBackgroundColor(Color.WHITE)
            }
        }

        override fun getItemCount(): Int = data.size
    }


    private fun getCargoTypeList() {
        val map = mapOf(Pair("", ""))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val cargoTypeListRes = Gson().fromJson(result, CargoTypeListRes::class.java)
                cargoTypes.clear()
                cargoTypes.add(CargoType(-1, "", "所有"))
                cargoTypes.addAll(cargoTypeListRes.retRes)
                sAdapter.notifyDataSetChanged()
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

        }, false).postRequest(activity as Context, KUCUN_TYPE.getInterface(), map)
    }

    private fun getCargoList(id: Int) {
        val map = mapOf(Pair("type_id", if (id >= 0) id.toString() else ""))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                val cargoKucunListRes = Gson().fromJson(result, CargoKucunListRes::class.java)
                cargoKucuns.clear()
                cargoKucuns.addAll(cargoKucunListRes.retRes)
                mAdapter.notifyDataSetChanged()
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

        }, false).postRequest(activity as Context, KUCUN_LISTS.getInterface(), map)
    }
}