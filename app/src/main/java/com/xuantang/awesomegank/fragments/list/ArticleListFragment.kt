package com.xuantang.awesomegank.fragments.list

import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.adapter.ArticleListAdapter
import com.xuantang.awesomegank.fragments.LazyFragment
import com.xuantang.awesomegank.viewmodel.NoImageArticleViewModel
import kotlinx.android.synthetic.main.fragment_article_list.*

class ArticleListFragment(private val category: String) : LazyFragment() {
    private var page: Int = 1
    private var hasMore: Boolean = true

    private val articleModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this).get(NoImageArticleViewModel::class.java)
    }

    override fun getData() {
        articleModel.getArticleData().observe(this, Observer {
            it.let {
                (article_recycle_view.adapter as ArticleListAdapter).setData(articleModel.getArticleData().value)
                article_recycle_view.adapter?.notifyDataSetChanged()
            }
        })
        articleModel.getError().observe(this, Observer {
            it.let {
                hasMore = false
                (article_recycle_view.adapter as ArticleListAdapter).setHasMore(hasMore)
                article_recycle_view.adapter?.notifyDataSetChanged()
            }
        })
        articleModel.fetch(category, page)
    }

    override fun initView() {
        article_recycle_view.adapter = ArticleListAdapter(this, articleModel.getArticleData().value, hasMore) {
            // loadMore
            articleModel.fetch(category, ++page)
        }

        article_recycle_view.layoutManager = LinearLayoutManager(context)

        article_toolbar.title = category


        article_toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_back, null)
        article_toolbar.setNavigationOnClickListener {
            activity?.finish()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_article_list
    }
}