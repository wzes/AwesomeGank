package com.wanglu.photoviewerlibrary

import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_preview.*

class PreviewActivity : AppCompatActivity() {

    private var mIndicatorType = PhotoViewer.INDICATOR_TYPE_DOT   // 默认type为小圆点

    private var currentPage = 0
    private var mCreatedInterface: PhotoViewer.OnPhotoViewerCreatedListener? = null
    private var mDestroyInterface: PhotoViewer.OnPhotoViewerDestroyListener? = null
    private val mDot = intArrayOf(R.drawable.no_selected_dot, R.drawable.selected_dot)
    private val mPhotoViewerFragments = mutableListOf<PhotoViewerFragment>()

    private var mDotGroup: LinearLayout? = null
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

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.statusBarColor = Color.TRANSPARENT
        var flag: Int = window.decorView.systemUiVisibility
        flag = flag or (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.decorView.systemUiVisibility = flag

        setContentView(R.layout.activity_preview)

        currentPage = mPreviewConfig?.currentPage ?: 0

        val decorView = window.decorView as ViewGroup

        val layoutTransition = LayoutTransition()
        val alphaOa = ObjectAnimator.ofFloat(decorView, "alpha", 0f, 1f)
        alphaOa.duration = 50
        layoutTransition.setAnimator(LayoutTransition.APPEARING, alphaOa)
        decorView.layoutTransition = layoutTransition

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
            val adapter = PhotoViewerPagerAdapter(mPhotoViewerFragments, supportFragmentManager)
            preview_viewpager.adapter = adapter
            preview_viewpager.currentItem = currentPage
            addOnPageChangeListener()


            if (mMediaModels.size in 2..9 && mIndicatorType == PhotoViewer.INDICATOR_TYPE_DOT) {
                mDotGroup = LinearLayout(this)
                if (mDotGroup!!.childCount != 0)
                    mDotGroup!!.removeAllViews()
                val dotParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                /**
                 * 未选中小圆点的间距
                 */
                dotParams.rightMargin = Utils.dp2px(this, 12)

                /**
                 * 创建未选中的小圆点
                 */
                for (i in mMediaModels.indices) {
                    val iv = ImageView(this)
                    iv.setImageDrawable(resources.getDrawable(mDot[0], theme))
                    iv.layoutParams = dotParams
                    mDotGroup!!.addView(iv)
                }

                /**
                 * 设置小圆点Group的方向为水平
                 */
                mDotGroup!!.orientation = LinearLayout.HORIZONTAL
                /**
                 * 设置小圆点在中间
                 */
                mDotGroup!!.gravity = Gravity.CENTER or Gravity.BOTTOM
                /**
                 * 两个Group的大小都为match_parent
                 */
                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )


                params.bottomMargin = Utils.dp2px(this, 70)

                /**
                 * 首先添加小圆点的Group
                 */
                preview_container.addView(mDotGroup, params)

                mDotGroup!!.post {
                    if (mSelectedDot != null) {
                        mSelectedDot = null
                    }
                    if (mSelectedDot == null) {
                        val iv = ImageView(this)
                        iv.setImageDrawable(resources.getDrawable(mDot[1], theme))

                        @Suppress("NAME_SHADOWING")
                        val paramst = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        /**
                         * 设置选中小圆点的左边距
                         */
                        paramst.leftMargin = mDotGroup!!.getChildAt(0).x.toInt()
                        iv.translationX = (dotParams.rightMargin * currentPage + mDotGroup!!.getChildAt(
                            0
                        ).width * currentPage).toFloat()
                        paramst.gravity = Gravity.BOTTOM
                        dot_container.addView(iv, paramst)
                        mSelectedDot = iv
                    }
                    dot_container.layoutParams = params
                }
            } else {
                tv = TextView(this)
                tv!!.text = "${currentPage + 1}/${mMediaModels.size}"
                tv!!.setTextColor(Color.WHITE)
                tv!!.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                tv!!.textSize = 18f
                dot_container.addView(tv)
                val params = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                params.bottomMargin = Utils.dp2px(this, 80)
                dot_container.layoutParams = params
            }
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
                    val dx = mDotGroup!!.getChildAt(1).x - mDotGroup!!.getChildAt(0).x
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