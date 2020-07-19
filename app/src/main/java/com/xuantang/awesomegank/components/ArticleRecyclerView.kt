package com.xuantang.awesomegank.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.xuantang.awesomegank.fragments.HomeFragment

class ArticleRecyclerView : RecyclerView {

    private var touch: Boolean = false
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        HomeFragment.Instance.get().addStickyListener(object : HomeFragment.OnStickyListener {
            override fun onChanged(sticky: Boolean) {
                touch = sticky
                isNestedScrollingEnabled = touch
            }
        })
        isNestedScrollingEnabled = false
    }
}