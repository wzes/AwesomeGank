package com.xuantang.awesomegank.fragments.me

import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.appbar.AppBarLayout
import com.xuantang.awesomegank.AppContext
import com.xuantang.awesomegank.R
import com.xuantang.basemodule.extentions.clearCache
import com.xuantang.basemodule.extentions.dispatchDefault
import com.xuantang.basemodule.extentions.getStatusBarHeight
import com.xuantang.basemodule.extentions.toast
import com.xuantang.basemodule.fragments.LazyFragment
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_collection.*
import kotlinx.android.synthetic.main.fragment_me.*

class MeFragment : LazyFragment() {

    override fun getData() {
        tv_about.setOnClickListener {
            toast("By 爨")
        }
        tv_clear.setOnClickListener {
            AlertDialog.Builder(activity!!)
                .setMessage("所有缓存都会被清除")
                .setNegativeButton("取消") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("确认") { dialog, _ ->
                    Observable
                        .create<Boolean> { emitter ->
                            val result = AppContext.clearCache()
                            emitter.onNext(result)
                        }
                        .dispatchDefault()
                        .subscribeBy(onNext = { result ->
                            dialog.dismiss()
                            toast(if (result) "清除成功" else "清除失败")
                        }, onError = { error ->
                            toast("${error.message}")
                        })
                }
                .create()
                .show()
        }
    }

    override fun initView() {
        val layoutParams = me_title.layoutParams as AppBarLayout.LayoutParams
        me_title.text = "我的"
        layoutParams.topMargin = context?.getStatusBarHeight()!!
        me_title.layoutParams = layoutParams
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_me
    }
}