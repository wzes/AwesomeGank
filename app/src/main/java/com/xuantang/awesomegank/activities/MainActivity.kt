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
import com.xuantang.awesomegank.fragments.HomeFragment
import com.xuantang.awesomegank.fragments.MeFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusTransAndDarkIcon(Color.TRANSPARENT)
        setContentView(R.layout.activity_main)
        viewpager.adapter = MainPagerAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewpager.offscreenPageLimit = 3
        bottom_nav.setOnNavigationItemSelectedListener { p0 ->
            viewpager.setCurrentItem(when (p0.itemId) {
                R.id.home -> 0
                R.id.read -> 1
                R.id.data -> 2
                R.id.me -> 3
                else -> 0
            }, false)
            true
        }
    }

    inner class MainPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()
                1 -> HomeFragment()
                2 -> HomeFragment()
                else -> MeFragment()
            }
        }

        override fun getCount() = 4
    }
}
