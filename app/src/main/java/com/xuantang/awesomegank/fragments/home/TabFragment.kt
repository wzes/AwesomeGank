package com.xuantang.awesomegank.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.adapter.ArticleAdapter
import com.xuantang.awesomegank.extentions.no
import com.xuantang.awesomegank.extentions.yes
import com.xuantang.awesomegank.viewmodel.ArticleViewModel
import com.xuantang.awesomegank.viewmodel.RefreshViewModel
import kotlinx.android.synthetic.main.fragment_tab.*


class TabFragment(private val category: String, private val position: Int) : Fragment(),
    HomeFragment.OnRefreshListener, HomeFragment.OnStickyListener {
    private var isInit = false
    private var isFirstVisible = true
    private val articleModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(ArticleViewModel::class.java)
    }

    private val refreshModel by lazy(LazyThreadSafetyMode.NONE) {
        activity?.let {
            ViewModelProviders.of(it).get(RefreshViewModel::class.java)
        }
    }

    private var page: Int = 1
    private var hasMore: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tab, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.home_tab_recyclerview)
        recyclerView.tag = "tab_$position"
        return view
    }

    private fun initView() {
        home_tab_recyclerview.adapter =
            context?.let {
                ArticleAdapter(it, articleModel.getArticleData().value, hasMore) {
                    // loadMore
                    articleModel.fetch(category, ++page)
                }
            }
        home_tab_recyclerview.layoutManager = LinearLayoutManager(context)
        HomeFragment.newInstance().addRefreshListener(this)
        HomeFragment.newInstance().addStickyListener(this)
    }

    private fun getData() {
        articleModel.getArticleData().observe(this, Observer {
            it.let {
                (home_tab_recyclerview.adapter as ArticleAdapter).setData(articleModel.getArticleData().value)
                home_tab_recyclerview.adapter?.notifyDataSetChanged()
                refreshModel?.setRefresh(0)
            }
        })
        articleModel.getError().observe(this, Observer {
            it.let {
                hasMore = false
                (home_tab_recyclerview.adapter as ArticleAdapter).setHasMore(hasMore)
                home_tab_recyclerview.adapter?.notifyDataSetChanged()
                refreshModel?.setRefresh(0)
            }
        })
        articleModel.fetch(category, page)
    }


    override fun onDestroy() {
        super.onDestroy()
        isFirstVisible = true
        isInit = false
    }

    override fun onResume() {
        super.onResume()
        isInit.no {
            initView()
            isInit = true
        }
        isFirstVisible.yes {
            getData()
            isFirstVisible = false
        }
    }

    override fun onRefresh(index: Int) {
        (index == position).yes {
            page = 1
            articleModel.fetch(category, page, true)
        }
    }

    override fun onChanged(sticky: Boolean) {
        if (!sticky) {
            if (home_tab_recyclerview.computeVerticalScrollOffset() != 0) {
                home_tab_recyclerview.smoothScrollToPosition(0)
            }
        }
    }
}