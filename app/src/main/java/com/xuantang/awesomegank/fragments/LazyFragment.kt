package com.xuantang.awesomegank.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.xuantang.awesomegank.extentions.no
import com.xuantang.awesomegank.extentions.yes

abstract class LazyFragment : Fragment() {
    private var isInit = false
    private var isFirstVisible = true

    abstract fun getData()
    abstract fun initView()

    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        isInit = false
        isFirstVisible = true
    }

    override fun onResume() {
        super.onResume()
        isInit.no {
            initView()
            isInit = true
        }
        isFirstVisible.yes {
            getData()
            isFirstVisible = false
        }
    }
}