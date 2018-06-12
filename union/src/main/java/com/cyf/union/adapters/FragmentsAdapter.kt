package com.cyf.union.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.cyf.lezu.fragments.BaseFragment

import java.util.ArrayList

/**
 * ChaYin
 * Created by 蔡雨峰 on 2017/9/18.
 */

class FragmentsAdapter(fm: FragmentManager, private val list: ArrayList<BaseFragment>, private val titles: Array<String>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return list[position]
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position < titles.size) titles[position] else ""
    }
}
