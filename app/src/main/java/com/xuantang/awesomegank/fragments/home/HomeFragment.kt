package com.xuantang.awesomegank.fragments.home

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.activities.MainActivity
import com.xuantang.awesomegank.adapter.ImageBannerAdapter
import com.xuantang.awesomegank.extentions.dp
import com.xuantang.awesomegank.extentions.getStatusBarHeight
import com.xuantang.awesomegank.fragments.LazyFragment
import com.xuantang.awesomegank.viewmodel.BannerViewModel
import com.xuantang.awesomegank.viewmodel.RefreshViewModel
import com.youth.banner.indicator.CircleIndicator
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : LazyFragment(), MainActivity.OnTabChangeListener {
    private val contentTopPadding: Lazy<Int?> = lazy { context?.getStatusBarHeight() }
    private var tabTitles = arrayOf("Android", "iOS", "Flutter", "前端", "后端", "App")
    private var tabCategories = arrayOf("Android", "iOS", "Flutter", "frontend", "backend", "app")

    private var pagerAdapter: FragmentStatePagerAdapter? = null
    private var onStickyListeners: ArrayList<OnStickyListener> = ArrayList()
    private var onRefreshListeners: ArrayList<OnRefreshListener> = ArrayList()

    private val bannerModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(BannerViewModel::class.java)
    }

    private val refreshModel by lazy(LazyThreadSafetyMode.NONE) {
        activity?.let {
            ViewModelProviders.of(it).get(RefreshViewModel::class.java)
        }
    }

    companion object {
        private var instance: HomeFragment? = null
        fun newInstance(): HomeFragment {
            if (instance == null) {
                instance =
                    HomeFragment()
            }
            return instance!!
        }
    }

    fun getCurrentRecyclerView(): RecyclerView? {
        return home_viewpager.findViewWithTag("tab_${home_viewpager.currentItem}")
    }

    override fun getData() {
        bannerModel.getBannerData().observe(this, Observer {
            it?.let { data ->
                banner.visibility = View.VISIBLE
                banner.addBannerLifecycleObserver(this)
                    .setAdapter(ImageBannerAdapter(data))
                    .setIndicator(CircleIndicator(context))
                    .start()
            }
        })
        bannerModel.getError().observe(this, Observer {
            banner.visibility = View.GONE
        })

        bannerModel.init()

        refreshModel?.getRefreshState()?.observe(this, Observer  {
            home_refresh.isRefreshing = it == 1
        })
    }

    fun addStickyListener(listener: OnStickyListener) {
        onStickyListeners.add(listener)
    }

    override fun initView() {
        val lp = search_btn.layoutParams as ViewGroup.MarginLayoutParams
        lp.topMargin = context?.dp(5)?.plus((this.contentTopPadding.value ?: 0)) ?: 10
        search_btn.setOnClickListener {
            Toast.makeText(context, "search ~", Toast.LENGTH_SHORT).show()
        }
        search_btn.text = "五月狂欢节🎉"

        val stickyMarginTop: Int = home_viewpager.dp(40 + 10) + lp.topMargin

        home_title_bar.apply {
            layoutParams.height = stickyMarginTop
        }

        home_viewpager.post {
            home_viewpager.apply {
                layoutParams.height = home_container.measuredHeight - stickyMarginTop - home_viewpager.dp(50)
            }
        }

        for (i in tabTitles.indices) {
            home_tab_layout.addTab(home_tab_layout.newTab())
        }

        home_tab_layout.setupWithViewPager(home_viewpager)

        if (activity?.supportFragmentManager != null) {
            pagerAdapter = FSPagerAdapter(
                activity?.supportFragmentManager!!,
                FragmentStatePagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT
            )
            home_viewpager.adapter = pagerAdapter
        }

        for (i in tabTitles.indices) {
            val tab = home_tab_layout.getTabAt(i)
            tab?.text = tabTitles[i]
        }

        home_refresh.setOnRefreshListener {
            refresh()
        }

        //  滑动监听
        var sticky: Boolean = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            home_scrollview.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                // 背景

                // 吸顶
                val tmp = scrollY >= stickyMarginTop
                if (tmp == sticky) {
                    return@setOnScrollChangeListener
                }
                sticky = tmp
                for (l in onStickyListeners) {
                    l.onChanged(sticky)
                }
            }
        }

        if (activity is MainActivity) {
            (activity as MainActivity).addOnTabChangeListener(this)
        }

        val viewFixed = LayoutInflater.from(context).inflate(R.layout.home_fixed, home_linear_container, false)
        home_linear_container.addView(viewFixed, 0)
    }

    private fun refresh() {
        refreshModel?.setRefresh(1)
        for (onRefreshListener in onRefreshListeners) {
            onRefreshListener.onRefresh(home_viewpager.currentItem)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    inner class FSPagerAdapter(supportFragmentManager: FragmentManager, behavior: Int) :
        FragmentStatePagerAdapter(supportFragmentManager, behavior) {
        override fun getItem(position: Int): Fragment {
            return TabFragment(tabCategories[position], position)
        }

        override fun getCount(): Int {
            return tabTitles.size
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        }
    }

    interface OnStickyListener {
        fun onChanged(sticky: Boolean)
    }

    interface OnRefreshListener {
        fun onRefresh(index: Int)
    }

    fun removeStickyListener(listener: OnStickyListener) {
        onStickyListeners.remove(listener)
    }

    fun addRefreshListener(listener: OnRefreshListener) {
        onRefreshListeners.add(listener)
    }

    fun removeRefreshListener(listener: OnRefreshListener) {
        onRefreshListeners.remove(listener)
    }

    override fun onTabSelected(index: Int, oldIndex: Int) {
        // 首页
        if (index == oldIndex && index == 0) {
            getCurrentRecyclerView()?.scrollToPosition(0)
            home_scrollview.scrollTo(0, 0)

            home_refresh.isRefreshing = true
            refresh()
        }
    }
}