package com.cyf.team.activities

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.cyf.lezu.MyBaseActivity
import com.cyf.lezu.utils.BottomNavigationViewHelper
import com.cyf.team.AppTeam
import com.cyf.team.R
import com.cyf.team.UserID
import com.cyf.team.fragments.*
import kotlinx.android.synthetic.main.activity_home_kg.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/12/1/001.
 */
class HomeKGActivity : MyBaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_mission -> {
                loadFragment(0)
                return true
            }
            R.id.navigation_mine -> {
                loadFragment(1)
                return true
            }
            R.id.navigation_order -> {
                loadFragment(2)
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_kg)
        init()
    }

    private val fragments = Array<Fragment?>(3){null}
    private var last = -1

    private fun init() {
        navigation.setOnNavigationItemSelectedListener(this)
        BottomNavigationViewHelper.disableShiftMode(navigation)
        if (AppTeam.isLogged) {
            when (AppTeam.logier_id) {
                UserID.KU_GUAN -> {
                    navigation.inflateMenu(R.menu.navigation_kg)
                }
                UserID.XUN_JIAN -> {
                    navigation.inflateMenu(R.menu.navigation_xj)
                }
                UserID.GONG_REN -> {
                    navigation.inflateMenu(R.menu.navigation_gr)
                }
            }
            loadFragment(0)
        }
    }

    private fun loadFragment(position:Int) {
        val ft = supportFragmentManager.beginTransaction()
        if(fragments[position] == null){
            when (AppTeam.logier_id) {
                UserID.KU_GUAN -> {
                    fragments[position] = when(position){
                        0->{
                            MissionKGFragment()
                        }
                        1->{
                            MineKGFragment()
                        }
                        2->{
                            OrderKGFragment()
                        }
                        else->{
                            OrderKGFragment()
                        }
                    }
                }
                UserID.XUN_JIAN -> {
                    fragments[position] = when(position){
                        0->{
                            MissionXJFragment()
                        }
                        1->{
                            MineXJFragment()
                        }
                        2->{
                            OrderXJFragment()
                        }
                        else->{
                            OrderXJFragment()
                        }
                    }
                }
                UserID.GONG_REN -> {
                    fragments[position] = when(position){
                        0->{
                            MissionGRFragment()
                        }
                        1->{
                            MineGRFragment()
                        }
                        2->{
                            MissionGRFragment()
                        }
                        else->{
                            MineGRFragment()
                        }
                    }
                }
            }
            ft.add(R.id.content, fragments[position])
        }
        if(last != -1){
            ft.hide(fragments[last])
        }
        ft.show(fragments[position])
        last = position
        ft.commit()
    }


}