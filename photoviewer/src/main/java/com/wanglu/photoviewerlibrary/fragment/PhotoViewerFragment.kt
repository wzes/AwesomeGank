package com.wanglu.photoviewerlibrary.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.KeyEvent
import android.view.View
import com.bumptech.glide.Glide
import com.wanglu.photoviewerlibrary.activity.OnLongClickListener
import com.wanglu.photoviewerlibrary.R
import com.xuantang.basemodule.extentions.yes
import com.xuantang.basemodule.fragments.LazyFragment
import kotlinx.android.synthetic.main.item_picture.*

class PhotoViewerFragment : LazyFragment() {
    var exitListener: OnExitListener? = null
    var longClickListener: OnLongClickListener? = null

    private var mImgSize = IntArray(2)
    private var mExitLocation = IntArray(2)
    private var mInAnim = true
    private var mPicData = ""

    fun setData(imgSize: IntArray, exitLocation: IntArray, picData: String, inAnim: Boolean) {
        mImgSize = imgSize
        mExitLocation = exitLocation
        mInAnim = inAnim
        mPicData = picData
    }

    interface OnExitListener {
        fun exit()
    }

    override fun getLayoutId(): Int {
        return R.layout.item_picture
    }

    override fun getData() {
    }

    override fun initView() {
        Glide.with(photo_view.context).load(mPicData).into(photo_view)
        photo_view.visibility = View.INVISIBLE
        var alpha = 1f
        photo_view.setExitLocation(mExitLocation)
        photo_view.setImgSize(mImgSize)
        photo_view.setOnLongClickListener {
            if (longClickListener != null) {
                longClickListener!!.onLongClick(it)
            }
            true
        }
        var intAlpha = 255
        photo_container.background.alpha = intAlpha
        photo_view.rootView = photo_container

        photo_view.setOnViewFingerUpListener {
            alpha = 1f
            intAlpha = 255
        }
        photo_view.setExitListener {
            if (exitListener != null) {
                exitListener!!.exit()
            }
        }
        mInAnim.yes {
            photo_view.post {
                photo_view.visibility = View.VISIBLE
                val scaleOx = ObjectAnimator.ofFloat(photo_view, "scale", mImgSize[0].toFloat() / photo_view.width, 1f)
                val xOa = ObjectAnimator.ofFloat(photo_view, "translationX", mExitLocation[0].toFloat() - photo_view.width / 2, 0f)
                val yOa = ObjectAnimator.ofFloat(photo_view, "translationY", mExitLocation[1].toFloat() - photo_view.height / 2, 0f)
                val set = AnimatorSet()
                set.duration = 250
                set.playTogether(scaleOx, xOa, yOa)
                set.start()
            }
        }
        photo_container.isFocusableInTouchMode = true
        photo_container.requestFocus()
        photo_container.setOnKeyListener(View.OnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                photo_view.exit()
                return@OnKeyListener true
            }
            false
        })

        photo_view.setOnViewDragListener { dx, dy ->
            photo_view.scrollBy((-dx).toInt(), (-dy).toInt())  // 移动图像
            alpha -= dy * 0.001f
            intAlpha -= (dy * 0.5).toInt()
            if (alpha > 1) alpha = 1f
            else if (alpha < 0) alpha = 0f
            if (intAlpha < 0) intAlpha = 0
            else if (intAlpha > 255) intAlpha = 255
            photo_container.background.alpha = intAlpha    // 更改透明度
            if (alpha >= 0.6)
                photo_view.attacher.scale = alpha   // 更改大小
        }
        photo_view.setOnClickListener {
            photo_view.exit()
        }
    }
}