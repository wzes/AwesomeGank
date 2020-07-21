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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.databinding.HomeItemViewBinding
import com.xuantang.awesomegank.extentions.no
import com.xuantang.awesomegank.extentions.yes
import com.xuantang.awesomegank.model.Data
import com.xuantang.awesomegank.service.DataService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_tab.*


class TabFragment(private val category: String, private val position: Int) : Fragment() {
    private var isInit = false
    private var isFirstVisible = true
    private var mData: MutableList<Data.Results>? = arrayListOf()

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
        HomeFragment.newInstance().addStickyListener(object : HomeFragment.OnStickyListener {
            override fun onChanged(sticky: Boolean) {
                if (!sticky) {
                    if (home_tab_recyclerview.computeVerticalScrollOffset() != 0) {
                        home_tab_recyclerview.smoothScrollToPosition(0)
                    }
                }
            }
        })
    }

    private fun getData() {
        CompositeDisposable().add(
            DataService.getDataOfType(category, 10, page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    if (it.error || it.results.isEmpty()) {
                        hasMore = false
                    } else {
                        this.mData?.addAll(it.results)
                        home_tab_recyclerview.adapter?.notifyDataSetChanged()
                    }
                })
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
                (mData?.size ?: 0) + 1
            } else {
                (mData?.size ?: 0)
            }
        }

        override fun getItemViewType(position: Int): Int {
            if (position == mData?.size) {
                return 2
            }
            return 1
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (getItemViewType(position) == 1) {
                mData?.get(position)?.let {
                    (holder as ArticleViewHolder).bind(it)
                }
            } else if (getItemViewType(position) == 2) {
                (holder as LoadingViewHolder).bind()
                page++
                getData()
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
}