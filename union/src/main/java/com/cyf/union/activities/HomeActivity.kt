package com.cyf.union.activities

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.utils.BottomNavigationViewHelper
import com.cyf.union.AppUnion
import com.cyf.union.R
import com.cyf.union.UserID
import com.cyf.union.fragments.*
import kotlinx.android.synthetic.main.activity_home.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/1/6/006.
 */
class HomeActivity : MyBaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_mission -> {
                loadFragment(fragments[0])
                return true
            }
            R.id.navigation_performance -> {
                loadFragment(fragments[1])
                return true
            }
            R.id.navigation_jixiao -> {
                loadFragment(fragments[0])
                return true
            }
            R.id.navigation_check -> {
                loadFragment(fragments[1])
                return true
            }
            R.id.boss_faquan -> {
                loadFragment(fragments[2])
                return true
            }
            R.id.worker_faquan -> {
                loadFragment(fragments[2])
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        init()
    }

    private val fragments = ArrayList<Fragment>()

    private fun init() {
        navigation.setOnNavigationItemSelectedListener(this)
        BottomNavigationViewHelper.disableShiftMode(navigation)
        if (AppUnion.isLogged) {
            if (AppUnion.logier_id == UserID.BOSS) {
                navigation.inflateMenu(R.menu.navigation_boss)
                fragments.add(BossQueryGradeFragment())
                fragments.add(BossQueryCheckFragment())
                fragments.add(FaQuanFragment())
            } else {
                navigation.inflateMenu(R.menu.navigation_worker)
                fragments.add(WorkSalePlatFragment())
                fragments.add(WorkGradeFragment())
                fragments.add(FaQuanFragment())
            }
            loadFragment(fragments[0])
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.content, fragment)
        ft.commit()
    }


}