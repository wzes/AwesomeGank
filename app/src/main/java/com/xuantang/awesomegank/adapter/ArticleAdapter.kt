package com.xuantang.awesomegank.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.components.NineGridImageLayout
import com.xuantang.awesomegank.databinding.HomeItemArticleAdapterBinding
import com.xuantang.awesomegank.model.ArticleResponse
import com.xuantang.basemodule.extentions.yes

class ArticleAdapter(private val context: Context,
                     private var data: List<ArticleResponse.ArticleModel>?,
                     private var hasMore: Boolean,
                     private var loadMore: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var imageClickListener: ((v: View, position: Int, imageList: List<String>, id: String, itemPosition: Int) -> Unit)? = null

    fun setHasMore(hasMore: Boolean) {
        this.hasMore = hasMore
    }

    fun setData(data: List<ArticleResponse.ArticleModel>?) {
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<HomeItemArticleAdapterBinding>(
                inflater,
                R.layout.home_item_article_adapter,
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
                    (holder as ArticleViewHolder).bind(it, position)
                }
            }
        } else if (getItemViewType(position) == 2) {
            (holder as LoadingViewHolder).bind()
            hasMore.yes {
                loadMore()
            }
        }
    }

    fun setOnImageClick(click: (v: View, position: Int, imageList: List<String>, id: String, itemPosition: Int) -> Unit) {
        imageClickListener = click
    }

    internal inner class ArticleViewHolder(private val binding: HomeItemArticleAdapterBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ArticleResponse.ArticleModel, position: Int) {
            binding.common = item
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                ARouter.getInstance()
                    .build("/web/")
                    .withString("web_url", "https://gank.io/post/${item._id}")
                    .withString("title", item.desc)
                    .withString("cover_url", if (item.images.isNotEmpty()) {
                        item.images[0]
                    } else {
                        ""
                    })
                    .navigation()
            }
            if (position == 0) {
                bindAfterExecute(binding, item, position)
            }
            bindAfterExecute(binding, item, position)
        }

        private fun bindAfterExecute(binding: HomeItemArticleAdapterBinding, item: ArticleResponse.ArticleModel, itemPosition: Int) {
            binding.layoutNineGrid.run {
                setImageList(item.images, 1f, item._id)
                onItemClick { v, position ->
                    imageClickListener?.invoke(v, position, item.images, item._id, itemPosition)
                }
                loadImages { view, url ->
                    Glide.with(context)
                        .load(url)
                        .apply(RequestOptions().placeholder(R.color.divider)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                        .into(view)

                }
            }
        }

        private fun NineGridImageLayout.setImageList(imageList: List<String>?, ratio: Float, id: String) {
            imageList?.isNotEmpty()?.let {
                this.setData(imageList, ratio, id)
            }
        }

    }

    internal inner class LoadingViewHolder(layout: LinearLayoutCompat) : RecyclerView.ViewHolder(layout) {
        private var mImageView: ImageView? = null
        init {
            mImageView = layout.findViewById(R.id.home_item_loading)
        }
        fun bind() {
            val rotateAnimation: Animation = AnimationUtils.loadAnimation(context, R.anim.home_loading_item)
            mImageView?.startAnimation(rotateAnimation)
        }
    }
}
