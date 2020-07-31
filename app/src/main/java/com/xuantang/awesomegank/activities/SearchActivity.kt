package com.xuantang.awesomegank.activities

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import com.alibaba.android.arouter.facade.annotation.Route
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.adapter.ArticleAdapter
import com.xuantang.awesomegank.extentions.getStatusBarHeight
import com.xuantang.awesomegank.extentions.hideKeyboard
import com.xuantang.awesomegank.extentions.setStatusTransAndDarkIcon
import com.xuantang.awesomegank.extentions.yes
import com.xuantang.awesomegank.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_search.*

@Route(path = "/search/")
class SearchActivity : AppCompatActivity() {
    private val contentTopPadding: Lazy<Int?> = lazy { this.getStatusBarHeight() }

    private val articleModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(SearchViewModel::class.java)
    }

    private var page: Int = 1
    private var hasMore: Boolean = true
    private var category: String = "All"
    private var key: String = ""

    private var keyboardViewVisible: Boolean = true

    private var articleAdapter: ArticleAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusTransAndDarkIcon(Color.TRANSPARENT)
        setContentView(R.layout.activity_search)

        search_header_layout.apply {
            (layoutParams as LinearLayoutCompat.LayoutParams).topMargin = contentTopPadding.value!!
        }

        search_cancel.setOnClickListener {
            finish()
        }

        search_edit.requestFocus()
        search_edit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // empty
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
                keyboardViewVisible = true
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                articleModel.clear()
                if (s != null && s.isNotEmpty()) {
                    key = s.toString()
                    page = 1
                    getData(key, category)
                }
            }
        })

        articleAdapter = ArticleAdapter(this, articleModel.getArticleData().value, hasMore) {
            // loadMore
            articleModel.fetch(key, category, page)
        }.apply {
            setHasMore(false)
        }
        search_recycle_view.adapter = articleAdapter
        search_recycle_view.layoutManager = LinearLayoutManager(this)

        search_recycle_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_DRAGGING) {
                    keyboardViewVisible.yes {
                        hideKeyboard()
                        search_edit.clearFocus()
                        keyboardViewVisible = false
                    }
                }
            }
        })
    }

    fun getData(key: String, category: String) {
        articleModel.getArticleData().observe(this, Observer {
            it.let {
                (search_recycle_view.adapter as ArticleAdapter).apply {
                    setHasMore(articleModel.getArticleData().value?.size!! > 0)
                    setData(articleModel.getArticleData().value)
                    notifyDataSetChanged()
                }
            }
        })
        articleModel.getError().observe(this, Observer {
            it.let {
                hasMore = false
                (search_recycle_view.adapter as ArticleAdapter).setHasMore(hasMore)
                search_recycle_view.adapter?.notifyDataSetChanged()
            }
        })
        articleModel.fetch(key, category, page)
    }
}