package com.xuantang.awesomegank.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wanglu.photoviewerlibrary.media.MediaModel
import com.wanglu.photoviewerlibrary.media.MediaStoreFactory
import com.wanglu.photoviewerlibrary.PhotoViewer
import com.wanglu.photoviewerlibrary.media.PreviewConfig
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.adapter.ArticleAdapter
import com.xuantang.basemodule.extentions.no
import com.xuantang.basemodule.extentions.yes
import com.xuantang.awesomegank.viewmodel.ArticleViewModel
import com.xuantang.awesomegank.viewmodel.RefreshViewModel
import com.xuantang.basemodule.extentions.toast
import kotlinx.android.synthetic.main.fragment_tab.*
import java.util.ArrayList


class TabFragment(private val category: String, private val position: Int) : Fragment(),
    HomeFragment.OnRefreshListener, HomeFragment.OnStickyListener {
    private var isInit = false
    private var isFirstVisible = true

    private var adapter: ArticleAdapter? = null
    private val articleModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this).get(ArticleViewModel::class.java)
    }

    private val refreshModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(requireActivity()).get(RefreshViewModel::class.java)
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
        context?.let {
            adapter = ArticleAdapter(it, articleModel.getArticleData().value, hasMore) {
                // loadMore
                articleModel.fetch(category, ++page)
            }
        }
        home_tab_recyclerview.adapter = adapter

        adapter?.setOnImageClick { _, position, imageList, id, itemPosition ->
            val mediaModels = imageList.mapIndexed { index, s ->
                val viewGroup = home_tab_recyclerview.layoutManager?.findViewByPosition(itemPosition)
                val view = viewGroup?.findViewWithTag<View>("$id-$index")
                if (view == null) {
                    toast("cannot find: $id-$index")
                    MediaModel(
                        url = s,
                        height = 0,
                        width = 0,
                        locationRec = IntArray(2)
                    )
                } else {
                    MediaModel(
                        url = s,
                        height = view.height,
                        width = view.width,
                        locationRec = getCurrentViewLocation(view)
                    )
                }
            } as ArrayList
            val key = MediaStoreFactory.setPayload(mediaModels)

            PhotoViewer.start(this, PreviewConfig(
                mediaModelKey = key,
                currentPage = position
            ))
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
                refreshModel.setRefresh(0)
            }
        })
        articleModel.getError().observe(this, Observer {
            it.let {
                hasMore = false
                (home_tab_recyclerview.adapter as ArticleAdapter).setHasMore(hasMore)
                home_tab_recyclerview.adapter?.notifyDataSetChanged()
                refreshModel.setRefresh(0)
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

    /**
     * 获取现在查看到的图片的原始位置 (中间)
     */
    private fun getCurrentViewLocation(view: View): IntArray {
        val result = IntArray(2)
        view.getLocationInWindow(result)
        result[0] += view.width / 2
        result[1] += view.height / 2
        return result
    }
}