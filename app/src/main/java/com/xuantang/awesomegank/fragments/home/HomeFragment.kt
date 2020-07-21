package com.xuantang.awesomegank.fragments.home

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aminography.redirectglide.GlideApp
import com.xuantang.awesomegank.App
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.components.HomeNestedScrollView
import com.xuantang.awesomegank.extentions.dp
import com.xuantang.awesomegank.extentions.getStatusBarHeight
import com.xuantang.awesomegank.fragments.LazyFragment
import com.xuantang.awesomegank.model.Banner
import com.xuantang.awesomegank.service.DataService
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.indicator.CircleIndicator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : LazyFragment() {
    private val contentTopPadding: Lazy<Int?> = lazy { context?.getStatusBarHeight() }
    private var tabTitles = arrayOf("Android", "iOS", "前端", "福利", "休息视频", "拓展资源")
    private var pagerAdapter: FragmentStatePagerAdapter? = null
    private var onStickyListeners: ArrayList<OnStickyListener> = ArrayList()

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
        CompositeDisposable().add(
            DataService.getBanners()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    banner.addBannerLifecycleObserver(this)
                        .setAdapter(
                            ImageAdapter(
                                it.data
                            )
                        )
                        .setIndicator(CircleIndicator(context))
                        .start()
                })
    }

    fun addStickyListener(listener: OnStickyListener) {
        onStickyListeners.add(listener)
    }

    fun removeStickyListener(listener: OnStickyListener) {
        onStickyListeners.remove(listener)
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
            pagerAdapter = FSPagerAdapter(activity?.supportFragmentManager!!, FragmentStatePagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT)
            home_viewpager.adapter = pagerAdapter
        }
        for (i in tabTitles.indices) {
            val tab = home_tab_layout.getTabAt(i)
            tab?.text = tabTitles[i]
        }

        home_scrollview.addPullListener(object : HomeNestedScrollView.OnPullListener {
            override fun onPull(down: Int) {
                Log.d("XT", "onPull: $down")
            }

            override fun onPullEnd() {
                Log.d("XT", "onPullEnd")
            }
        })

        home_refresh.setOnRefreshListener {
            home_refresh.postDelayed({
                home_refresh.isRefreshing = false
            }, 1000)
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
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    inner class FSPagerAdapter(supportFragmentManager: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(supportFragmentManager, behavior) {
        override fun getItem(position: Int): Fragment {
            return TabFragment(tabTitles[position], position)
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

    internal class ImageAdapter(mData: List<Banner.Item>) :
        BannerAdapter<Banner.Item, ImageAdapter.BannerViewHolder>(mData) {
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

        internal inner class BannerViewHolder(var imageView: ImageView) :
            RecyclerView.ViewHolder(imageView)
    }
}