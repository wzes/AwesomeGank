package com.xuantang.awesomegank.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.recyclerview.widget.RecyclerView
import com.aminography.redirectglide.GlideApp
import com.google.android.material.tabs.TabLayout
import com.xuantang.awesomegank.App
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.extentions.dp
import com.xuantang.awesomegank.extentions.getStatusBarHeight
import com.xuantang.awesomegank.fragments.home.TabFragment
import com.xuantang.awesomegank.model.Banner
import com.youth.banner.adapter.BannerAdapter
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : LazyFragment() {
    private val contentTopPadding: Lazy<Int?> = lazy { context?.getStatusBarHeight() }
    private val tabTitles = arrayOf("推荐", "Android", "iOS", "Flutter", "前端", "福利", "休息视频")
    private var pagerAdapter: FragmentStatePagerAdapter? = null
    private var onStickyListeners: ArrayList<OnStickyListener> = ArrayList()
    
    object Instance {
        private var instance: HomeFragment? = null

        fun get(): HomeFragment {
            if (instance == null) {
                instance = HomeFragment()
            }
            return instance!!
        }
    }
    
    override fun getData() {
//        CompositeDisposable().add(DataService.getBanners()
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe {
//                banner.addBannerLifecycleObserver(this)
//                    .setAdapter(ImageAdapter(it.data))
//                    .setIndicator(CircleIndicator(context))
//                    .start()
//            })
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

        home_tab_layout.setTabTextColors(Color.BLACK, Color.BLUE)
        home_tab_layout.setSelectedTabIndicatorColor(Color.BLUE)

        val stickyMarginTop: Int = home_viewpager.dp(50 + 40) + lp.topMargin
        home_viewpager.post {
            home_viewpager.apply {
                layoutParams = LinearLayoutCompat.LayoutParams(home_container.measuredWidth, home_container.measuredHeight - stickyMarginTop)
            }
        }

        for (i in tabTitles.indices) {
            home_tab_layout.addTab(home_tab_layout.newTab())
        }
        home_tab_layout.tabMode = TabLayout.MODE_SCROLLABLE
        home_tab_layout.setupWithViewPager(home_viewpager, false)
        if (activity?.supportFragmentManager != null) {
            pagerAdapter = FSPagerAdapter(activity?.supportFragmentManager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
            home_viewpager.adapter = pagerAdapter
        }
        for (i in tabTitles.indices) {
            val tab = home_tab_layout.getTabAt(i)
            tab?.text = tabTitles[i]
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            home_scrollview.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                if (scrollY > 200) {
                   for (l in onStickyListeners) {
                       l.onChanged(true)
                   }
                } else {
                    for (l in onStickyListeners) {
                        l.onChanged(false)
                    }
                }
            }
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    inner class FSPagerAdapter(supportFragmentManager: FragmentManager, behavior: Int) :
        FragmentStatePagerAdapter(supportFragmentManager, behavior) {
        override fun getItem(position: Int): Fragment {
            return TabFragment(tabTitles[position])
        }
        override fun getCount(): Int {
            return tabTitles.size
        }
    }

    internal class ImageAdapter(mData: List<Banner.Item>) : BannerAdapter<Banner.Item, ImageAdapter.BannerViewHolder>(mData) {
        override fun onCreateHolder(
            parent: ViewGroup,
            viewType: Int
        ): BannerViewHolder {
            val imageView = ImageView(parent.context)
            imageView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            return BannerViewHolder(imageView)
        }

        @SuppressLint("CheckResult")
        override fun onBindView(
            holder: BannerViewHolder,
            data: Banner.Item,
            position: Int,
            size: Int
        ) {
            GlideApp.with(App.INSTANCE).load(data.image).into(holder.imageView)
        }
        internal inner class BannerViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)
    }

    interface OnStickyListener {
        fun onChanged(sticky: Boolean)
    }
}