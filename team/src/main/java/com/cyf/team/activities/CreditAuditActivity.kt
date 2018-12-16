package com.cyf.team.activities

import android.os.Bundle
import com.cyf.lezu.MyBaseActivity
import com.cyf.team.R
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_credit_audit.*
import android.widget.Toast
import com.cyf.lezu.adapters.LineDecoration
import com.cyf.lezu.initActionBar
import com.cyf.lezu.utils.CalendarUtil
import com.cyf.team.AppTeam
import com.cyf.team.entity.CreditInfo
import com.cyf.team.entity.IMAGE_URL
import com.cyf.team.entity.PersonalQual
import com.cyf.team.entity.PersonalQualTemp
import com.squareup.picasso.Picasso
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener
import kotlinx.android.synthetic.main.activity_userinfo_audit.*
import kotlinx.android.synthetic.main.fragment_audit.*
import kotlinx.android.synthetic.main.layout_list_body.view.*
import kotlinx.android.synthetic.main.layout_list_header.view.*


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/16/016.
 */
class CreditAuditActivity : MyBaseActivity() {

    private val personalQuals = ArrayList<PersonalQualTemp>()
    private var adapter = AnimalsHeadersAdapter()
    private var headersDecor: StickyRecyclerHeadersDecoration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_audit)
        initViews(AppTeam.creditInfo)
        initEvent()
    }

    private fun initViews(creditInfo: CreditInfo) {
        if (creditInfo.type_id == 1) {
            initActionBar(this, "个人信用", true)
            getData(creditInfo.grLists)
        } else {
            initActionBar(this, "企业信用", true)
            getData(creditInfo.gsLists)
        }
        // 为RecyclerView设置适配器
        adapter.addAll(personalQuals)
        auditCredits.adapter = adapter
        // 为RecyclerView添加LayoutManager
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        auditCredits.layoutManager = layoutManager
        // 为RecyclerView添加Decorator装饰器
        // 为RecyclerView中的Item添加Header头部（自动获取头部ID，将相邻的ID相同的聚合到一起形成一个Header）
        headersDecor = StickyRecyclerHeadersDecoration(adapter)
        auditCredits.addItemDecoration(headersDecor)
        // 为RecyclerView添加Item之间的分隔线
        auditCredits.addItemDecoration(LineDecoration(this, LineDecoration.VERTICAL))
    }

    private fun getData(data: ArrayList<PersonalQual>) {
        personalQuals.clear()
        for (i in 0 until data.size) {
            val p = data[i]
            if (p.lists.size <= 0) {
                val per = PersonalQualTemp(i + 101, p.id, p.account_id, p.score, p.max, p.title, "", 0)
                personalQuals.add(per)
            } else {
                for (j in 0 until p.lists.size) {
                    val per = PersonalQualTemp(i + 101, p.id, p.account_id, p.score, p.max, p.title, p.lists[j].file_url, p.lists[j].create_time)
                    personalQuals.add(per)
                }
            }
        }
    }

    private fun initEvent() {
        // 为RecyclerView添加普通Item的点击事件（点击Header无效）
        auditCredits.addOnItemTouchListener(RecyclerItemClickListener(this, RecyclerItemClickListener.OnItemClickListener { view, position -> Toast.makeText(view.context, adapter.getItem(position).toString() + " Clicked", Toast.LENGTH_SHORT).show() }))
        // 为RecyclerView添加Header的点击事件
        val touchListener = StickyRecyclerHeadersTouchListener(auditCredits, headersDecor)
        touchListener.setOnHeaderClickListener { header, position, headerId ->
            Toast.makeText(header.context,
                    "Header position: $position, id: $headerId", Toast.LENGTH_SHORT).show()
        }
        auditCredits.addOnItemTouchListener(touchListener)
    }


    // StickyHeadersRecyclerView的适配器类
    private inner class AnimalsHeadersAdapter : AnimalAdapter<RecyclerView.ViewHolder>(), StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_body, parent, false)
            return object : RecyclerView.ViewHolder(view) {

            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val personalQual = getItem(position)
            if (personalQual.file_url == "") {
//                holder.itemView.scoreImage.visibility = View.GONE
//                holder.itemView.scoreTime.visibility = View.GONE
            } else {
                Picasso.with(holder.itemView.context).load(IMAGE_URL + personalQual.file_url).into(holder.itemView.scoreImage)
                holder.itemView.scoreTime.text = CalendarUtil(personalQual.create_time, true).format(CalendarUtil.STANDARD)
            }
        }

        // 获取当前Item的首字母，按照首字母将相邻的Item聚集起来并添加统一的头部
        override fun getHeaderId(position: Int): Long {
            return getItem(position).id_title.toLong()
        }

        // 获取头部布局
        override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_header, parent, false)
            return object : RecyclerView.ViewHolder(view) {

            }
        }

        // 为头部布局中的控件绑定数据
        override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val personalQual = getItem(position)
            holder.itemView.scoreName.text = personalQual.title
            holder.itemView.scoreCurr.text = "当前得分${personalQual.score}/${personalQual.max}"
            holder.itemView.scoreBtn.setOnClickListener {

            }
        }
    }
}