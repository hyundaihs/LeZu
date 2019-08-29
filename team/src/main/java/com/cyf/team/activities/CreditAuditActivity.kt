package com.cyf.team.activities

import android.content.Context
import android.graphics.Rect
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
import com.cyf.lezu.requests.MySimpleRequest
import com.cyf.lezu.toast
import com.cyf.lezu.utils.CalendarUtil
import com.cyf.team.AppTeam
import com.cyf.team.entity.*
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_userinfo_audit.*
import kotlinx.android.synthetic.main.fragment_audit.*
import kotlinx.android.synthetic.main.layout_list_body.view.*
import kotlinx.android.synthetic.main.layout_list_header.view.*
import android.view.MotionEvent
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.cyf.lezu.E


/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/16/016.
 */
class CreditAuditActivity : MyBaseActivity() {

    private val personalQuals = ArrayList<PersonalQualTemp>()
    private var adapter = AnimalsHeadersAdapter()
    private var headersDecor: StickyRecyclerHeadersDecoration? = null
    private val scores = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_audit)
        initViews(AppTeam.creditInfo)
        initEvent()
        PickerUtil.initChooseType(scores)
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
                val per = PersonalQualTemp(i + 1, p.id, p.account_id, p.score, p.max, p.title, "", 0)
                personalQuals.add(per)
            } else {
                for (j in 0 until p.lists.size) {
                    val per = PersonalQualTemp(i + 1, p.id, p.account_id, p.score, p.max, p.title, p.lists[j].file_url, p.lists[j].create_time)
                    personalQuals.add(per)
                }
            }
        }
    }

    private fun initEvent() {
        // 为RecyclerView添加普通Item的点击事件（点击Header无效）
        auditCredits.addOnItemTouchListener(RecyclerItemClickListener(this, RecyclerItemClickListener.OnItemClickListener { view, position -> }))
        // 为RecyclerView添加Header的点击事件
        val touchListener = StickyRecyclerHeadersTouchListener(auditCredits, headersDecor)
        touchListener.setOnHeaderClickListener { header, position, headerId, e ->
            if (inRangeOfView(header.scoreBtn, e)) {
                if (header.scoreEdit.text.isEmpty()) {
                    toast("分数不能为空")
                } else {
                    setScore(header.scoreBtn, personalQuals[position].id, header.scoreEdit.text.toString())
                }
            }
            if (inRangeOfView(header.scoreEdit, e)) {
                PickerUtil.showChooseType(header.context, "选择分数") { options1, options2, options3, v ->
                    header.scoreEdit.setText(scores[options1].toString())
                }
            }
        }
        auditCredits.addOnItemTouchListener(touchListener)
    }

    private fun inRangeOfView(view: View, ev: MotionEvent): Boolean {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        var x = location[0]
        var y = location[1]
        if (x == 0 && y == 0) {
            val rect = Rect()
            view.getGlobalVisibleRect(rect)
            x = rect.left
            y = rect.top
        }
        val ex = ev.x
        val ey = ev.y
        E("x = $x  y = $y")
        E("ex = ${ex}  ey = ${ey}")
        return !(ex < x || ex > x + view.width)
    }


    // StickyHeadersRecyclerView的适配器类
    private inner class AnimalsHeadersAdapter : AnimalAdapter<RecyclerView.ViewHolder>(), StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(com.cyf.team.R.layout.layout_list_body, parent, false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val personalQual = getItem(position)
            if (personalQual.file_url != "") {
                Picasso.with(holder.itemView.context).load(IMAGE_URL + personalQual.file_url).resize(800,800).into(holder.itemView.scoreImage)
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
            return MyViewHolder(view)
        }

        // 为头部布局中的控件绑定数据
        override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val personalQual = getItem(position)
            holder.itemView.scoreName.text = personalQual.title
            holder.itemView.scoreCurr.text = "当前得分${personalQual.score}/${personalQual.max}"
            holder.itemView.scoreBtn.setOnClickListener {
                if (holder.itemView.scoreEdit.text.isEmpty()) {
                    toast("分数不能为空")
                } else {
                    setScore(holder.itemView.scoreBtn, personalQual.id, holder.itemView.scoreEdit.text.toString())
                }
            }
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun setScore(view: View, id: Int, score: String) {
        val map = mapOf(
                Pair("id", id),
                Pair("score", score)
        )
        MySimpleRequest(object : MySimpleRequest.RequestCallBack {
            override fun onSuccess(context: Context, result: String) {
                toast("操作成功")
                view.visibility = View.GONE
            }

            override fun onError(context: Context, error: String) {
                toast(error)
            }

            override fun onLoginErr(context: Context) {

            }

        }, false).postRequest(this, SETSCORE.getInterface(), map)
    }
}