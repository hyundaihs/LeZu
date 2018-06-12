package com.cyf.union.activities

import android.os.Bundle
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.fragments.BaseFragment
import com.cyf.lezu.initActionBar
import com.cyf.union.R
import com.cyf.union.adapters.FragmentsAdapter
import com.cyf.union.fragments.FaquanLogFragment
import kotlinx.android.synthetic.main.activity_faquan_log.*
import java.util.ArrayList

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/4/14/014.
 */
class FaQuanLogActivity : MyBaseActivity() {

    private val titles = arrayOf("未使用", "已使用", "已过期")
    private var list = ArrayList<BaseFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faquan_log)
        initActionBar(this, "发放记录")
        initViews()
    }

    private fun initViews() {
        for (i in 0 until 3) {
            val bundle = Bundle()
            bundle.putInt("type", i + 1)
            val faquanLogFragment = FaquanLogFragment()
            faquanLogFragment.arguments = bundle
            list.add(faquanLogFragment)
        }
        val adapter = FragmentsAdapter(supportFragmentManager, list, titles)
        faquan_log_viewpager.adapter = adapter
        faquan_log_tab_layout.setupWithViewPager(faquan_log_viewpager)

    }

}