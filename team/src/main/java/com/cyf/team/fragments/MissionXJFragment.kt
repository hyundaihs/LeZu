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
import kotlinx.android.synthetic.main.fragment_mission_kg.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/1/001.
 */
class MissionXJFragment  : BaseFragment() {
    var login_verf: String by PreferenceUtil(AppTeam.instance, PreferenceUtil.LOGIN_VERF, "")
    var m_Account: String by PreferenceUtil(AppTeam.instance, PreferenceUtil.ACCOUNT, "")
    var m_Password: String by PreferenceUtil(AppTeam.instance, PreferenceUtil.PASSWORD, "")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mission_xj, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViews()
    }

    override fun onResume() {
        super.onResume()
        activity?.initActionBar(activity as AppCompatActivity, "资料审核", false,rightBtn = "退出", rightClick = {
            login_verf = ""
            m_Account = ""
            m_Password = ""
            AppTeam.isLogged = false
            startActivity(Intent(context, MainActivity::class.java))
        })
    }

    private fun initViews() {
        val fragments = ArrayList<Fragment>()
        val titles = ArrayList<String>()
        val ordersFragment1 = AuditUserInfoFragment()
        fragments.add(ordersFragment1)
        val ordersFragment2 = AuditCreditFragment()
        fragments.add(ordersFragment2)
        titles.add("身份审核")
        titles.add("信用审核")
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