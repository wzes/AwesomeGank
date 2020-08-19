package com.xuantang.awesomegank.fragments.home

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.aminography.redirectglide.GlideApp
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.activities.MainActivity
import com.xuantang.awesomegank.adapter.ImageBannerAdapter
import com.xuantang.awesomegank.components.HomeNestedScrollView
import com.xuantang.awesomegank.extentions.dp
import com.xuantang.awesomegank.extentions.getStatusBarHeight
import com.xuantang.awesomegank.extentions.no
import com.xuantang.awesomegank.fragments.LazyFragment
import com.xuantang.awesomegank.model.KingKong
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

    private var showFuliAnimator: ObjectAnimator? = null
    private var hideFuliAnimator: ObjectAnimator? = null
    private var isFuliHiding: Boolean = false
    private var isFuliShowing: Boolean = true
    private var sticky: Boolean = false
    private var curScrollTime = System.currentTimeMillis()

    private val kingKongs: ArrayList<KingKong> = arrayListOf(
        KingKong(
            "Android",
            "/article/list",
            "https://png.pngtree.com/png-vector/20191025/ourlarge/pngtree-beautiful-android-logo-vector-glyph-icon-png-image_1869973.jpg",
            "Android"
        ),
        KingKong(
            "iOS",
            "/article/list",
            "https://png.pngtree.com/png-vector/20191025/ourlarge/pngtree-beautiful-android-logo-vector-glyph-icon-png-image_1869973.jpg",
            "iOS"
        ),
        KingKong(
            "前端",
            "/article/list",
            "https://png.pngtree.com/png-vector/20191025/ourlarge/pngtree-beautiful-android-logo-vector-glyph-icon-png-image_1869973.jpg",
            "前端"
        ),
        KingKong(
            "休息视频",
            "/article/list",
            "https://png.pngtree.com/png-vector/20191025/ourlarge/pngtree-beautiful-android-logo-vector-glyph-icon-png-image_1869973.jpg",
            "休息视频"
        ),
        KingKong(
            "拓展资源",
            "/article/list",
            "https://png.pngtree.com/png-vector/20191025/ourlarge/pngtree-beautiful-android-logo-vector-glyph-icon-png-image_1869973.jpg",
            "拓展资源"
        )
    )

    private val bannerModel by lazy(LazyThreadSafetyMode.NONE) {
        defaultViewModelProviderFactory.create(BannerViewModel::class.java)
    }

    private val refreshModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(requireActivity()).get(RefreshViewModel::class.java)
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
        bannerModel.getData().observe(this, Observer {
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

        refreshModel.getRefreshState().observe(this, Observer {
            home_refresh.isRefreshing = it == 1
        })
    }

    fun addStickyListener(listener: OnStickyListener) {
        onStickyListeners.add(listener)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        val lp = search_btn.layoutParams as ViewGroup.MarginLayoutParams
        lp.topMargin = context?.dp(5)?.plus((this.contentTopPadding.value ?: 0)) ?: 10
        search_btn.setOnClickListener {
            ARouter.getInstance().build("/search/")
                .navigation()
        }
        search_btn.text = "五月狂欢节🎉"

        val stickyMarginTop: Int = home_viewpager.dp(40 + 10) + lp.topMargin

        home_title_bar.apply {
            layoutParams.height = stickyMarginTop
        }

        home_viewpager.post {
            home_viewpager.apply {
                layoutParams.height =
                    home_container.measuredHeight - stickyMarginTop - home_viewpager.dp(50)
            }
        }

        for (i in tabTitles.indices) {
            home_tab_layout.addTab(home_tab_layout.newTab())
        }

        home_tab_layout.setupWithViewPager(home_viewpager)

        if (activity?.supportFragmentManager != null) {
            pagerAdapter = FSPagerAdapter(
                activity?.supportFragmentManager!!,
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
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

        home_fuli.setOnClickListener {
            ARouter.getInstance().build("/fuli/")
                .navigation()
        }

        home_scrollview.addTouchListener(object : HomeNestedScrollView.OnTouchListener {
            override fun onTouch(ev: MotionEvent?) {
                // 处理福利
                home_scrollview.postDelayed({
                    isFuliHiding.no {
                        isFuliHiding = true
                        hideFuliImageAnimation(context!!)
                    }
                }, 500)

                curScrollTime = System.currentTimeMillis()
                home_scrollview.postDelayed({
                    val diff = System.currentTimeMillis() - curScrollTime
                    if (diff < 2000) {
                        return@postDelayed
                    }
                    isFuliShowing.no {
                        isFuliShowing = true
                        showFuliImageAnimation(context!!)
                    }
                }, 2000)
            }
        })
        //  滑动监听
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

        // 金刚位
        home_linear_container.addView(getKingKongView(), 0)

        // 福利
        home_container.post {
            home_fuli.apply {
                val layoutParams = layoutParams as FrameLayout.LayoutParams
                layoutParams.topMargin = home_container.bottom - dp(100)
                layoutParams.leftMargin = home_container.right - dp(55)
                radius = dp(50).toFloat()
            }
        }
    }

    private fun hideFuliImageAnimation(context: Context) {
        hideFuliAnimator = ObjectAnimator.ofFloat(home_fuli, "translationX", 0f, context.dp(30f).toFloat()).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 500
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    home_fuli.alpha = 0.5f
                    isFuliShowing = false
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
        }
        hideFuliAnimator?.start()
    }

    private fun showFuliImageAnimation(context: Context) {
        showFuliAnimator = ObjectAnimator.ofFloat(home_fuli, "translationX", context.dp(30f).toFloat(), context.dp(0f).toFloat()).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 500
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    home_fuli.alpha = 1f
                    isFuliHiding = false
                }

                override fun onAnimationCancel(animation: Animator?) {
                    isFuliHiding = false
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
        }
        showFuliAnimator?.start()
    }

    private fun getKingKongView(): View {
        val root = LinearLayoutCompat(context).apply {
            orientation = LinearLayoutCompat.HORIZONTAL
        }
        for (kingKong in kingKongs) {
            val item: LinearLayoutCompat = LayoutInflater.from(context)
                .inflate(R.layout.home_fixed_item, root, false) as LinearLayoutCompat
            item.apply {
                (layoutParams as LinearLayoutCompat.LayoutParams).weight = 1f
                layoutParams.width = 0
            }
            val textView = item.findViewById<AppCompatTextView>(R.id.kingkong_title)
            textView.text = kingKong.title
            GlideApp.with(context!!).load(kingKong.icon)
                .into(item.findViewById<AppCompatImageView>(R.id.kingkong_image))
            item.setOnClickListener {
                ARouter.getInstance().build(kingKong.scheme)
                    .withString("category", kingKong.category)
                    .withString("title", kingKong.title)
                    .navigation()
            }
            root.addView(item)
        }
        return root
    }

    private fun refresh() {
        refreshModel.setRefresh(1)
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