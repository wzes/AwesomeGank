package com.xuantang.awesomegank.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.aminography.redirectglide.GlideApp
import com.xuantang.awesomegank.App
import com.xuantang.awesomegank.model.Banner
import com.youth.banner.adapter.BannerAdapter

class ImageBannerAdapter(mData: List<Banner.Item>) : BannerAdapter<Banner.Item, ImageBannerAdapter.BannerViewHolder>(mData) {
    override fun onCreateHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageBannerAdapter.BannerViewHolder {
        val imageView = ImageView(parent.context)
        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return BannerViewHolder(imageView)
    }

    @SuppressLint("CheckResult")
    override fun onBindView(
        holder: BannerViewHolder,
        data: Banner.Item,
        position: Int,
        size: Int
    ) {
        GlideApp.with(App.INSTANCE).load(data.image).into(holder.imageView)
    }

    inner class BannerViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}