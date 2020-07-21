package com.xuantang.awesomegank.fragments.fuli

import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.fragments.LazyFragment

class FuLiFragment : LazyFragment() {
    override fun getData() {
    }

    override fun initView() {

    }

    companion object {
        private var instance: FuLiFragment? = null

        fun newInstance(): FuLiFragment {
            if (instance == null) {
                instance =
                    FuLiFragment()
            }
            return instance!!
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_me
    }
}