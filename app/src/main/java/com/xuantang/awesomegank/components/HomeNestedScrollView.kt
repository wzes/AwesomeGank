package com.xuantang.awesomegank.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import com.xuantang.awesomegank.fragments.home.HomeFragment


class HomeNestedScrollView : NestedScrollView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var mPullDownY: Int = 0
    private var mOnPullListeners: ArrayList<OnPullListener> = ArrayList()

    private var mVelocityTracker: VelocityTracker? = null
    fun addPullListener(l: OnPullListener) {
        this.mOnPullListeners.add(l)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (dy > 0) {
            if (canScrollVertically(1)) {
                consumed[1] = dy
                scrollBy(0, dy)
            }
        } else {
            if (!canScrollVertically(-1)) {
                mPullDownY -= dy
                // height 变大
                for (l in mOnPullListeners) {
                    l.onPull(mPullDownY)
                }
            } else {
                mPullDownY = 0
            }
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        if (target is ArticleRecyclerView && dyUnconsumed > 0) {
            val recyclerView = HomeFragment.newInstance().getCurrentRecyclerView()
            recyclerView?.scrollBy(dxUnconsumed, dyUnconsumed)
        }
    }

    override fun stopNestedScroll(type: Int) {
        super.stopNestedScroll(type)
        mPullDownY = 0
    }

    override fun onStopNestedScroll(target: View) {
        super.onStopNestedScroll(target)
        mPullDownY = 0
    }

    private var mLastY = 0

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        initVelocityTrackerIfNotExists()
        mVelocityTracker?.addMovement(ev)
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastY = ev.y.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                if (!canScrollVertically(1)) {
                    val target = HomeFragment.newInstance().getCurrentRecyclerView()
                    val dy = ev.y.toInt() - mLastY
                    target?.scrollBy(0, -dy)
                }
                mLastY = ev.y.toInt()
            }
            MotionEvent.ACTION_UP -> {
                for (l in mOnPullListeners) {
                    l.onPullEnd()
                }
                mVelocityTracker?.computeCurrentVelocity(1000)
                val target = HomeFragment.newInstance().getCurrentRecyclerView()
                if (mVelocityTracker?.yVelocity != null) {
                    target?.fling(0, -mVelocityTracker?.yVelocity?.toInt()!!)
                }
                recycleVelocityTracker()
            }
        }
        return super.onTouchEvent(ev)
    }


    private fun initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
    }

    private fun recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker?.recycle()
            mVelocityTracker = null
        }
    }

    interface OnPullListener {
        fun onPull(down: Int)
        fun onPullEnd()
    }
}