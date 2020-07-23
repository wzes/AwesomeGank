package com.xuantang.awesomegank.activities

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.extentions.setStatusTransAndDarkIcon
import com.xuantang.awesomegank.fragments.find.FindFragment
import com.xuantang.awesomegank.fragments.fuli.FuLiFragment
import com.xuantang.awesomegank.fragments.home.HomeFragment
import com.xuantang.awesomegank.fragments.me.MeFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val onTabSelectedListeners: ArrayList<OnTabChangeListener> = arrayListOf()
    private var tabIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusTransAndDarkIcon(Color.TRANSPARENT)
        setContentView(R.layout.activity_main)
        viewpager.adapter = MainPagerAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewpager.offscreenPageLimit = 3


        bottom_nav.setOnNavigationItemSelectedListener { p0 ->
            val position = when (p0.itemId) {
                R.id.home -> 0
                R.id.read -> 1
                R.id.data -> 2
                R.id.me -> 3
                else -> 0
            }
            for (l in onTabSelectedListeners) {
                l.onTabSelected(position, tabIndex)
            }
            tabIndex = position
            viewpager.setCurrentItem(position, false)
            true
        }

    }

    fun setBottomNavVisible(visibility: Int) {
        bottom_nav.visibility = visibility
    }

    inner class MainPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment.newInstance()
                1 -> FuLiFragment.newInstance()
                2 -> FindFragment()
                else -> MeFragment()
            }
        }

        override fun getCount() = 4
    }

    fun addOnTabChangeListener(onTabChangeListener: OnTabChangeListener) {
        onTabSelectedListeners.add(onTabChangeListener)
    }

    fun removeOnTabChangeListener(onTabChangeListener : OnTabChangeListener) {
        onTabSelectedListeners.remove(onTabChangeListener)
    }

    interface OnTabChangeListener {
        fun onTabSelected(index: Int, oldIndex: Int)
    }
}
