package com.xuantang.awesomegank.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xuantang.awesomegank.adapter.components.BannerViewHolder
import com.xuantang.awesomegank.model.ArticleResponse

class HomeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val data: List<ArticleResponse.ArticleModel>? = null

    override fun getItemCount(): Int {
        return this.data?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BannerViewHolder.createViewHolder(parent.context)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }
}