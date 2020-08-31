package com.wanglu.photoviewerlibrary.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wanglu.photoviewerlibrary.*
import com.wanglu.photoviewerlibrary.adapter.PhotoViewerPagerAdapter
import com.wanglu.photoviewerlibrary.fragment.PhotoViewerFragment
import com.wanglu.photoviewerlibrary.media.MediaStoreFactory
import com.wanglu.photoviewerlibrary.media.PreviewConfig
import com.xuantang.basemodule.extentions.dp
import com.xuantang.basemodule.extentions.setStatusTransparent
import kotlinx.android.synthetic.main.activity_preview.*

class PreviewActivity : AppCompatActivity() {

    private var mIndicatorType = PhotoViewer.INDICATOR_TYPE_DOT   // 默认type为小圆点

    private var currentPage = 0
    private var mCreatedInterface: PhotoViewer.OnPhotoViewerCreatedListener? = null
    private var mDestroyInterface: PhotoViewer.OnPhotoViewerDestroyListener? = null
    private val mDot = intArrayOf(R.drawable.no_selected_dot, R.drawable.selected_dot)
    private val mPhotoViewerFragments = mutableListOf<PhotoViewerFragment>()
    private var mSelectedDot: View? = null
    private var tv: TextView? = null

    private val mPreviewConfig by lazy(LazyThreadSafetyMode.NONE) {
        intent.getParcelableExtra<PreviewConfig>(PhotoViewer.PREVIEW_CONFIG)
    }

    private val mMediaModels by lazy(LazyThreadSafetyMode.NONE) {
        MediaStoreFactory.getPayload(mPreviewConfig?.mediaModelKey!!)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "ObjectAnimatorBinding")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusTransparent()
        setContentView(R.layout.activity_preview)

        currentPage = mPreviewConfig?.currentPage ?: 0
        for (mediaModel in mMediaModels) {
            val photoViewerFragment = PhotoViewerFragment()
            photoViewerFragment.exitListener = object : PhotoViewerFragment.OnExitListener {
                override fun exit() {
                    runOnUiThread {
                        finish()
                        MediaStoreFactory.clearPayload(mPreviewConfig.mediaModelKey)
                        mDestroyInterface?.onDestroy()
                    }
                }

            }
            photoViewerFragment.setData(
                intArrayOf(mediaModel.width, mediaModel.height),
                mediaModel.locationRec,
                mediaModel.url,
                true
            )
            mPhotoViewerFragments.add(photoViewerFragment)
        }
        val adapter = PhotoViewerPagerAdapter(mPhotoViewerFragments, supportFragmentManager)
        preview_viewpager.adapter = adapter
        preview_viewpager.currentItem = currentPage
        addOnPageChangeListener()

        if (mMediaModels.size in 2..9 && mIndicatorType == PhotoViewer.INDICATOR_TYPE_DOT) {
            dot_group_container.visibility = View.VISIBLE
            dot_container.visibility = View.VISIBLE
            dot_text_view.visibility = View.GONE

            if (dot_group_container.childCount != 0) {
                dot_group_container.removeAllViews()
            }
            val dotParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dotParams.rightMargin = dp(12)
            for (i in mMediaModels.indices) {
                val iv = ImageView(this)
                iv.setImageDrawable(resources.getDrawable(mDot[0], theme))
                iv.layoutParams = dotParams
                dot_group_container.addView(iv)
            }

            dot_group_container.post {
                val paramsTmp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                paramsTmp.leftMargin = dot_group_container.getChildAt(0).x.toInt()
                dot_selected_img.translationX = (dotParams.rightMargin * currentPage + dot_group_container.getChildAt(0).width * currentPage).toFloat()
                paramsTmp.gravity = Gravity.BOTTOM
                dot_selected_img.layoutParams = paramsTmp
                mSelectedDot = dot_selected_img
            }
        } else {
            dot_group_container.visibility = View.GONE
            dot_container.visibility = View.VISIBLE
            dot_text_view.visibility = View.VISIBLE
            dot_text_view.text = "${currentPage + 1}/${mMediaModels.size}"
            tv = dot_text_view
        }
        mCreatedInterface?.onCreated()
    }

    private fun addOnPageChangeListener() {
        preview_viewpager.addOnPageChangeListener(object :
            androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (mSelectedDot != null && mMediaModels.size > 1) {
                    val dx = dot_group_container.getChildAt(1).x - dot_group_container.getChildAt(0).x
                    mSelectedDot!!.translationX = (position * dx) + positionOffset * dx
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                currentPage = position
                /**
                 * 设置文字版本当前页的值
                 */
                if (tv != null) {
                    tv!!.text = "${currentPage + 1}/${mMediaModels.size}"
                }
                val mediaModel = mMediaModels[position]
                mPhotoViewerFragments[currentPage].setData(
                    intArrayOf(
                        mediaModel.width,
                        mediaModel.height
                    ), mediaModel.locationRec, mediaModel.url, false
                )
            }

        })
    }

    fun setOnPhotoViewerCreatedListener(l: () -> Unit) {
        this.mCreatedInterface = object : PhotoViewer.OnPhotoViewerCreatedListener {
            override fun onCreated() {
                l()
            }

        }
    }

    fun setOnPhotoViewerDestroyListener(l: () -> Unit) {
        this.mDestroyInterface = object : PhotoViewer.OnPhotoViewerDestroyListener {
            override fun onDestroy() {
                l()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}