package com.xuantang.awesomegank.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.databinding.ArticleItemViewBinding
import com.xuantang.awesomegank.extentions.yes
import com.xuantang.awesomegank.model.NoImageArticleResponse

class ArticleListAdapter(private val fragment: Fragment,
                         private var data: List<NoImageArticleResponse.ArticleModel>?,
                         private var hasMore: Boolean,
                         private var loadMore: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun setHasMore(hasMore: Boolean) {
        this.hasMore = hasMore
    }

    fun setData(data: List<NoImageArticleResponse.ArticleModel>?) {
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ArticleItemViewBinding>(
                inflater,
                R.layout.article_item_view,
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
            (data?.size ?: 0) + 1
        } else {
            (data?.size ?: 0)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val size = data?.size ?: 0
        if (position == size) {
            return 2
        }
        return 1
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1) {
            data?.get(position).let {
                if (it != null) {
                    (holder as ArticleViewHolder).bind(it)
                }
            }
        } else if (getItemViewType(position) == 2) {
            (holder as LoadingViewHolder).bind()
            hasMore.yes {
                loadMore()
            }
        }
    }

    internal inner class ArticleViewHolder(private val binding: ArticleItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoImageArticleResponse.ArticleModel) {
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
            val rotateAnimation: Animation = AnimationUtils.loadAnimation(fragment.context, R.anim.home_loading_item)
            mImageView?.startAnimation(rotateAnimation)
        }
    }
}
