package com.cyf.union.fragments

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
import com.cyf.lezu.D
import com.cyf.lezu.adapters.LineDecoration
import com.cyf.lezu.entity.*
import com.cyf.lezu.fragments.BaseFragment
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.sendToWx
import com.cyf.lezu.toast
import com.cyf.lezu.utils.BottomDialog
import com.cyf.lezu.utils.LoginErrDialog
import com.cyf.lezu.utils.MyProgressDialog
import com.cyf.lezu.utils.ShowImageDialog
import com.cyf.lezu.widget.GlideImageLoader
import com.cyf.union.AppUnion
import com.cyf.union.R
import com.cyf.union.activities.LoginActivity
import com.cyf.union.activities.WebActivity
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlinx.android.synthetic.main.fragment_worker_sale.*
import kotlinx.android.synthetic.main.layout_wx_share.view.*
import kotlinx.android.synthetic.main.work_saleplat_list_item.view.*
import org.jetbrains.anko.toast

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/1/6/006.
 * 员工分销平台
 */
class WorkSalePlatFragment : BaseFragment() {
    val data = ArrayList<Goods>()
    val banners = ArrayList<Banner>()
    private val adapter = MyAdapter(data)

    companion object {
        var workerDetails: WorkerDetails? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_worker_sale, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerView()
        getStoreDetails()
        getWorkerDetails()
    }

    private fun getStoreDetails() {
        val map = mapOf(Pair("", ""))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(result: String) {
                val storeDetailsWorkerRes = Gson().fromJson(result, StoreDetailsWorkerRes::class.java)
                banners.clear()
                banners.addAll(storeDetailsWorkerRes.retRes.banner)
                initBanner()
                middleInfo.setText("乐租合作商:${storeDetailsWorkerRes.retRes.info.title}\n展品地址:${storeDetailsWorkerRes.retRes.info.address}")
                data.clear()
                data.addAll(storeDetailsWorkerRes.retRes.lists)
                adapter.notifyDataSetChanged()
            }

            override fun onError(error: String) {
                activity?.toast(error)
            }

            override fun onLoginErr() {
                activity?.LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(activity as Context, MySimpleRequest.ZT_INDEX, map)
    }

    private fun initBanner() {
        val images = ArrayList<String>()
        for (i in 0 until banners.size) {
            images.add(MySimpleRequest.IMAGE_URL + banners[i].file_url)
        }
        banner.setImages(images).setImageLoader(GlideImageLoader()).start()
        banner.setOnBannerListener {
            val banner = banners[it]
            val intent = Intent(activity, WebActivity::class.java)
            if (banner.http_url == "") {
                intent.putExtra("type", 0)
                intent.putExtra("html", banner.contents)
            } else {
                intent.putExtra("type", 1)
                intent.putExtra("html", banner.http_url)
                intent.putExtra("title", banner.goods_title)
                intent.putExtra("price1", banner.zl_price1)
                intent.putExtra("price2", banner.price)
                intent.putExtra("url", banner.file_url)
            }
            startActivity(intent)
        }
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        layoutManager.orientation = OrientationHelper.VERTICAL
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(LineDecoration(activity, LineDecoration.VERTICAL))
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.isNestedScrollingEnabled = false
    }


    private class MyAdapter(val data: ArrayList<Goods>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val goods = data[position]
            val picasso = Picasso.with(holder.itemView.context)
            picasso.load(MySimpleRequest.IMAGE_URL + goods.file_url).into(holder.itemView.goodsImage)
            holder.itemView.goodsName.text = goods.title
            holder.itemView.goodsRent.text = "租赁:¥${goods.zl_price1}-¥${goods.zl_price2}/月"
//            holder.itemView.goodsDeposit.text = "押金:¥${goods.deposit}"
            holder.itemView.goodsPrice.text = "购买:¥${goods.price}"
            holder.itemView.share.setOnClickListener {
                val view = LayoutInflater.from(holder.itemView.context).inflate(R.layout.layout_wx_share, null, false)
                val dialog = holder.itemView.context.BottomDialog(view)

                view.pyq.setOnClickListener {
                    dialog.dismiss()
                    val pDialog = MyProgressDialog(holder.itemView.context)
                    doAsync {
                        val bitmap = picasso.load(MySimpleRequest.IMAGE_URL + goods.file_url).get()
                        uiThread {
                            val rel = AppUnion.instance.api.sendToWx(goods.title, goods.zl_price1, goods.price, goods.http_url, bmp = bitmap, isTimeLine = true)
                            pDialog.dismiss()
                            D("发送${if (rel) "成功" else "失败"}")
                        }
                    }
                }
                view.wxhy.setOnClickListener {
                    dialog.dismiss()
                    val pDialog = MyProgressDialog(holder.itemView.context)
                    doAsync {
                        val bitmap = picasso.load(MySimpleRequest.IMAGE_URL + goods.file_url).get()
                        uiThread {
                            val rel = AppUnion.instance.api.sendToWx(goods.title, goods.zl_price1, goods.price, goods.http_url, bmp = bitmap, isTimeLine = false)
                            D("发送${if (rel) "成功" else "失败"}")
                            pDialog.dismiss()
                        }
                    }
                }
                view.ewm.setOnClickListener {
                    dialog.dismiss()
                    if (null != workerDetails)
                        holder.itemView.context.ShowImageDialog(workerDetails!!.ewm_file_url)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.work_saleplat_list_item, parent, false))
        }

        override fun getItemCount(): Int = data.size

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

    private fun getWorkerDetails() {
        val map = mapOf(Pair("", ""))
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(result: String) {
                val workerDetailsRes = Gson().fromJson(result, WorkerDetailsRes::class.java)
                workerDetails = workerDetailsRes.retRes
            }

            override fun onError(error: String) {
                activity?.toast(error)
            }

            override fun onLoginErr() {
                activity?.LoginErrDialog(DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                })
            }

        }).postRequest(activity as Context, MySimpleRequest.YGINFO, map)
    }

}

