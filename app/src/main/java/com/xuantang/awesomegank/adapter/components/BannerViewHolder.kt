package com.xuantang.awesomegank.adapter.components

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.xuantang.awesomegank.R

object BannerViewHolder {
    fun createViewHolder(ctx: Context): RecyclerView.ViewHolder {
        return BannerViewHolder(View.inflate(ctx, R.layout.activity_main, null))
    }

    class BannerViewHolder(var view: View) : RecyclerView.ViewHolder(view)
}