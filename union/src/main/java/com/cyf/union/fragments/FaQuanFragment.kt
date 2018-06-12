package com.cyf.union.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.cyf.lezu.fragments.BaseFragment
import com.cyf.lezu.initActionBar
import com.cyf.union.R
import com.cyf.union.activities.FaQuanActivity
import com.cyf.union.activities.FaQuanLogActivity
import kotlinx.android.synthetic.main.fragment_faquan.*

/**
 * ChaYin
 * Created by ${蔡雨峰} on 2018/4/14/014.
 */
class FaQuanFragment : BaseFragment() {

    private val dataYH = listOf(10, 20, 50, 100, 150, 200, 300)
    private val dataDY = listOf(100, 200, 300, 500, 1000, 1500, 2000)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_faquan, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.initActionBar(activity as AppCompatActivity, "发券中心", false, "发放记录", {
            startActivity(Intent(activity, FaQuanLogActivity::class.java))
        })
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val adapterYH = ArrayAdapter<Int>(activity, R.layout.fragment_faquan_list_item, R.id.fragment_faquan_list_item_text, dataYH)
        val adapterDY = ArrayAdapter<Int>(activity, R.layout.fragment_faquan_list_item, R.id.fragment_faquan_list_item_text, dataDY)
        fragment_faquan_youhui.adapter = adapterYH
        fragment_faquan_diyong.adapter = adapterDY
        val intent = Intent(activity, FaQuanActivity::class.java)
        fragment_faquan_youhui.setOnItemClickListener { parent, view, position, id ->
            intent.putExtra("price", dataYH[position])
            intent.putExtra("type", 1)
            startActivity(intent)
        }
        fragment_faquan_diyong.setOnItemClickListener { parent, view, position, id ->
            intent.putExtra("price", dataDY[position])
            intent.putExtra("type", 2)
            startActivity(intent)
        }

    }


}