package com.xuantang.awesomegank.fragments.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.databinding.HomeItemViewBinding
import com.xuantang.awesomegank.extentions.dispatchDefault
import com.xuantang.awesomegank.extentions.no
import com.xuantang.awesomegank.extentions.yes
import com.xuantang.awesomegank.model.Data
import com.xuantang.awesomegank.service.DataService
import com.xuantang.awesomegank.viewmodel.ArticleViewModel
import com.xuantang.awesomegank.viewmodel.BannerViewModel
import com.xuantang.awesomegank.viewmodel.RefreshViewModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
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
        home_tab_recyclerview.adapter = ArticleAdapter()
        home_tab_recyclerview.layoutManager = LinearLayoutManager(context)
        HomeFragment.newInstance().addRefreshListener(this)
        HomeFragment.newInstance().addStickyListener(this)
    }

    private fun getData() {
        articleModel.getArticleData().observe(this, Observer {
            it.let {
                home_tab_recyclerview.adapter?.notifyDataSetChanged()
                refreshModel?.setRefresh(0)
            }
        })
        articleModel.getError().observe(this, Observer {
            it.let {
                hasMore = false
                home_tab_recyclerview.adapter?.notifyDataSetChanged()
                refreshModel?.setRefresh(0)
            }
        })
        articleModel.fetch(category, page)
    }


    override fun onDestroy() {
        super.onDestroy()
        isFirstVisible = true
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

    inner class ArticleAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == 1) {
                val inflater = LayoutInflater.from(parent.context)
                val binding = DataBindingUtil.inflate<HomeItemViewBinding>(
                    inflater,
                    R.layout.home_item_view,
                    parent,
                    false
                )
                ArticleViewHolder(binding)
            } else {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.home_item_loading, parent, false) as LinearLayoutCompat
                LoadingViewHolder(view)
            }
        }

        override fun getItemCount(): Int {
            return if (hasMore) {
                (articleModel.getArticleData().value?.size ?: 0) + 1
            } else {
                (articleModel.getArticleData().value?.size ?: 0)
            }
        }

        override fun getItemViewType(position: Int): Int {
            val size = articleModel.getArticleData().value?.size ?: 0
            if (position == size) {
                return 2
            }
            return 1
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (getItemViewType(position) == 1) {
                articleModel.getArticleData().value?.get(position)?.let {
                    (holder as ArticleViewHolder).bind(it)
                }
            } else if (getItemViewType(position) == 2) {
                (holder as LoadingViewHolder).bind()
                articleModel.fetch(category, ++page)
            }
        }
    }

    internal inner class ArticleViewHolder(private val binding: HomeItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Data.Results) {
            binding.data = item
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                ARouter.getInstance().build("/web/")
                    .withString("web_url", item.url)
                    .withString("title", item.desc)
                    .navigation()
            }
        }
    }

    internal inner class LoadingViewHolder(layout: LinearLayoutCompat) : RecyclerView.ViewHolder(layout) {
        var mImageView: ImageView? = null
        init {
            mImageView = layout.findViewById(R.id.home_item_loading)
        }
        fun bind() {
            val rotateAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.home_loading_item)
            mImageView?.startAnimation(rotateAnimation)
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