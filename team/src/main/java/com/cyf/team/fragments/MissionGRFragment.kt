package com.cyf.team.fragments

import android.content.Intent
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
import com.cyf.lezu.utils.PreferenceUtil
import com.cyf.team.AppTeam
import com.cyf.team.MainActivity
import com.cyf.team.OrderListType
import com.cyf.team.R
import com.cyf.team.activities.HistoryOrderActivity
import kotlinx.android.synthetic.main.fragment_mission_kg.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/1/001.
 */
class MissionGRFragment  : BaseFragment() {

    var login_verf: String by PreferenceUtil(AppTeam.instance, PreferenceUtil.LOGIN_VERF, "")
    var m_Account: String by PreferenceUtil(AppTeam.instance, PreferenceUtil.ACCOUNT, "")
    var m_Password: String by PreferenceUtil(AppTeam.instance, PreferenceUtil.PASSWORD, "")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mission_kg, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViews()
    }

    override fun onResume() {
        super.onResume()
        activity?.initActionBar(activity as AppCompatActivity, "订单任务", false, rightBtn = "退出", rightClick = {
            login_verf = ""
            m_Account = ""
            m_Password = ""
            AppTeam.isLogged = false
            startActivity(Intent(context, MainActivity::class.java))
        }, leftBtn = "历史记录", leftClick = {
            val intent = Intent(it.context, HistoryOrderActivity::class.java)
            intent.putExtra("out",OrderListType.GR_HISTORY_OUT)
            intent.putExtra("in",OrderListType.GR_HISTORY_IN)
            startActivity(intent)
        })
    }

    private fun initViews() {
        val fragments = ArrayList<Fragment>()
        val titles = ArrayList<String>()
        val ordersFragment1 = OrdersFragment()
        val bundle1 = Bundle()
        bundle1.putInt("pageType", OrderListType.GR_ORDER_OUT)
        ordersFragment1.arguments = bundle1
        fragments.add(ordersFragment1)
        val ordersFragment2 = OrdersFragment()
        val bundle2 = Bundle()
        bundle2.putInt("pageType", OrderListType.GR_ORDER_IN)
        ordersFragment2.arguments = bundle2
        fragments.add(ordersFragment2)
        titles.add("出库订单")
        titles.add("入库订单")
        viewpager.adapter = MyPagerAdapter(childFragmentManager, fragments, titles)
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