package com.cyf.team.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.initActionBar
import com.cyf.team.OrderListType
import com.cyf.team.R
import com.cyf.team.fragments.MissionKGFragment
import com.cyf.team.fragments.OrdersFragment
import kotlinx.android.synthetic.main.fragment_mission_kg.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2019/8/15/015.
 */
class HistoryOrderActivity : MyBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_order)
        initActionBar(this,"历史记录")
        initViews()
    }

    private fun initViews() {
        val fragments = ArrayList<Fragment>()
        val titles = ArrayList<String>()
        val ordersFragment1 = OrdersFragment()
        val bundle1 = Bundle()
        bundle1.putInt("pageType", OrderListType.KG_HISTORY_OUT)
        ordersFragment1.arguments = bundle1
        fragments.add(ordersFragment1)
        val ordersFragment2 = OrdersFragment()
        val bundle2 = Bundle()
        bundle2.putInt("pageType", OrderListType.KG_HISTORY_IN)
        ordersFragment2.arguments = bundle2
        fragments.add(ordersFragment2)
        titles.add("出库订单")
        titles.add("入库订单")
        viewpager.adapter = MyPagerAdapter(supportFragmentManager, fragments, titles)
        tabLayout.setupWithViewPager(viewpager)//此方法就是让tablayout和ViewPager联动
    }

    private class MyPagerAdapter(fm: FragmentManager?, val fragments: List<Fragment>, val titles: List<String>) : FragmentPagerAdapter(fm) {

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): android.support.v4.app.Fragment {
            return fragments[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }
}