package com.xuantang.awesomegank.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.aminography.redirectglide.GlideApp
import com.xuantang.awesomegank.App
import com.xuantang.awesomegank.components.RoundCornerImageView
import com.xuantang.awesomegank.extentions.dp
import com.xuantang.awesomegank.model.Banner
import com.youth.banner.adapter.BannerAdapter

class ImageBannerAdapter(mData: List<Banner.Item>) : BannerAdapter<Banner.Item, ImageBannerAdapter.BannerViewHolder>(mData) {
    override fun onCreateHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageBannerAdapter.BannerViewHolder {
        val imageView = RoundCornerImageView(parent.context)
        imageView.apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            radius = context.dp(10).toFloat()
        }
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
        holder.imageView.setOnClickListener {
            ARouter.getInstance().build("/web/")
                .withString("web_url", data.url)
                .withString("title", data.title)
                .navigation()
        }
    }

    inner class BannerViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}