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
import com.cyf.team.OrderListType.Companion.KG_ORDER_ALL
import com.cyf.team.OrderListType.Companion.KG_ORDER_CANCEL
import com.cyf.team.OrderListType.Companion.KG_ORDER_CONFIRMED
import com.cyf.team.OrderListType.Companion.KG_ORDER_IN
import com.cyf.team.OrderListType.Companion.KG_ORDER_SEND
import com.cyf.team.OrderListType.Companion.KG_ORDER_WAIT_CONFIRM
import com.cyf.team.R
import kotlinx.android.synthetic.main.fragment_order_kg.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/1/001.
 */
class OrderKGFragment : BaseFragment() {

    companion object {
        val TYPES = listOf("所有", "待确认", "已确认", "已发货", "已取消", "已入库")
        val PAGES = listOf(KG_ORDER_ALL, KG_ORDER_WAIT_CONFIRM, KG_ORDER_CONFIRMED, KG_ORDER_SEND, KG_ORDER_CANCEL, KG_ORDER_IN)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_kg, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.initActionBar(activity as AppCompatActivity, "采购订单", false)
        initViews()
    }

    private fun initViews() {
        val fragments = ArrayList<Fragment>()
        val titles = ArrayList<String>()
        for (i in 0 until TYPES.size) {
            val ordersFragment1 = OrdersFragment()
            val bundle1 = Bundle()
            bundle1.putInt("pageType", PAGES[i])
            ordersFragment1.arguments = bundle1
            fragments.add(ordersFragment1)
            titles.add(TYPES[i])
        }
        viewpagerOrder.adapter = MyPagerAdapter(fragmentManager, fragments, titles)
        tabLayoutOrder.setupWithViewPager(viewpagerOrder)//此方法就是让tablayout和ViewPager联动
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