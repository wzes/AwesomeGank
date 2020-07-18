package com.xuantang.awesomegank.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.adapter.components.BannerViewHolder
import com.xuantang.awesomegank.model.Data
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val data: List<Data.Results>? = null

    override fun getItemCount(): Int {
        return this.data?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BannerViewHolder.createViewHolder(this.context)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }
}