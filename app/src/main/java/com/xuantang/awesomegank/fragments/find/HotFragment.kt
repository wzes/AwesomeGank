package com.xuantang.awesomegank.fragments.find

import android.graphics.Color
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aminography.redirectglide.GlideApp
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.xuantang.awesomegank.App
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.adapter.HotAdapter
import com.xuantang.awesomegank.extentions.getStatusBarHeight
import com.xuantang.awesomegank.fragments.LazyFragment
import com.xuantang.awesomegank.viewmodel.HotViewModel
import kotlinx.android.synthetic.main.fragment_find.*

class HotFragment : LazyFragment() {
    private val hotModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this).get(HotViewModel::class.java)
    }

    companion object {
        private var instance: HotFragment? = null
        fun newInstance(): HotFragment {
            if (instance == null) {
                instance =
                    HotFragment()
            }
            return instance!!
        }
    }

    private var hotAdapter: HotAdapter ? = null

    override fun getData() {
        hotModel.getHotData().observe(this, Observer {
            it?.let {
                hotAdapter?.setData(it)
                hotAdapter?.notifyDataSetChanged()
            }
        })

        hotModel.fetch("Article", 20)
    }

    override fun initView() {
        collapsing_toolbar_layout.title = ""
        collapsing_toolbar_layout.setExpandedTitleColor(Color.TRANSPARENT)
        find_toolbar.title = "本周最热文章"
        find_toolbar.apply {
            (layoutParams as CollapsingToolbarLayout.LayoutParams).topMargin = context?.getStatusBarHeight() ?: 0
        }
        GlideApp.with(App.INSTANCE)
            .load("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1849614389,1014522746&fm=26&gp=0.jpg")
            .into(find_image_view)
        hotAdapter = context?.let { HotAdapter(it, this.hotModel.getHotData().value) }

        find_recycler_view.adapter = hotAdapter
        find_recycler_view.layoutManager = LinearLayoutManager(context)

        find_recycler_view.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_find
    }
}