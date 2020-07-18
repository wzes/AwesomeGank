package com.xuantang.awesomegank.fragments

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.aminography.redirectglide.GlideApp
import com.xuantang.awesomegank.App
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.extentions.dp
import com.xuantang.awesomegank.extentions.getStatusBarHeight
import com.xuantang.awesomegank.extentions.px
import com.xuantang.awesomegank.model.Banner
import com.xuantang.awesomegank.service.DataService
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.indicator.CircleIndicator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : LazyFragment() {
    private val contentTopPadding: Lazy<Int?> = lazy { context?.getStatusBarHeight() }

    override fun getData() {
        CompositeDisposable().add(DataService.getBanners()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                banner.addBannerLifecycleObserver(this)
                    .setAdapter(ImageAdapter(it.data))
                    .setIndicator(CircleIndicator(context))
                    .start()
            })
    }

    override fun initView() {
        val lp = search_btn.layoutParams as ViewGroup.MarginLayoutParams
        lp.topMargin = context?.dp(5)?.plus((this.contentTopPadding.value ?: 0)) ?: 10
        search_btn.setOnClickListener {

        }
        search_btn.text = "五月狂欢节🎉"
}

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    internal class ImageAdapter(mDatas: List<Banner.Item>) : BannerAdapter<Banner.Item, ImageAdapter.BannerViewHolder>(mDatas) {
        override fun onCreateHolder(
            parent: ViewGroup,
            viewType: Int
        ): BannerViewHolder {
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

        internal inner class BannerViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)
    }
}