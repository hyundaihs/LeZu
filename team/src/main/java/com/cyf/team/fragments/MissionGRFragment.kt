package com.cyf.team.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cyf.lezu.fragments.BaseFragment
import com.cyf.lezu.initActionBar
import com.cyf.team.OrderListType
import com.cyf.team.R
import kotlinx.android.synthetic.main.fragment_mission_kg.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/1/001.
 */
class MissionGRFragment  : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mission_kg, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.initActionBar(activity as AppCompatActivity, "订单任务", false, rightBtn = "退出", rightClick = {

        })
        initViews()
    }

    private fun initViews() {
        val fragments = ArrayList<Fragment>()
        val titles = ArrayList<String>()
        val ordersFragment1 = OrdersFragment()
        val bundle1 = Bundle()
        bundle1.putInt("pageType", OrderListType.XJ_ORDER_OUT)
        ordersFragment1.arguments = bundle1
        fragments.add(ordersFragment1)
        val ordersFragment2 = OrdersFragment()
        val bundle2 = Bundle()
        bundle2.putInt("pageType", OrderListType.XJ_ORDER_IN)
        ordersFragment2.arguments = bundle2
        fragments.add(ordersFragment2)
        titles.add("出库订单")
        titles.add("入库订单")
        viewpager.adapter = MyPagerAdapter(fragmentManager, fragments, titles)
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