package com.xuantang.awesomegank.fragments.fuli

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.adapter.ImageAdapter
import com.xuantang.awesomegank.extentions.dp
import com.xuantang.awesomegank.extentions.getStatusBarHeight
import com.xuantang.awesomegank.extentions.otherwise
import com.xuantang.awesomegank.extentions.yes
import com.xuantang.awesomegank.fragments.LazyFragment
import com.xuantang.awesomegank.viewmodel.FuliViewModel
import kotlinx.android.synthetic.main.fragment_fuli.*

class FuLiFragment : LazyFragment() {
    private val contentTopPadding: Lazy<Int?> = lazy { context?.getStatusBarHeight() }


    private var page = 1
    private var mNavVisible: Boolean = false
    private var mImageAdapter: ImageAdapter? = null
    private val fuliModel by lazy(LazyThreadSafetyMode.NONE) {
        defaultViewModelProviderFactory.create(FuliViewModel::class.java)
    }

    override fun getData() {
        fuliModel.getFuliData().observe(this, Observer {
            it?.let {
                mImageAdapter?.setData(it.map { it1 ->
                    it1.images[0]
                })
                mImageAdapter?.notifyDataSetChanged()
            }
        })

        fuliModel.fetch(page)
    }

    override fun initView() {

        fuli_titlerbar.apply {
            layoutParams.height = contentTopPadding.value?.plus(dp(40)) ?: dp(40)
        }

        fuli_title.isSelected = true

        val lp = fuli_title.layoutParams as ViewGroup.MarginLayoutParams
        lp.topMargin = this.contentTopPadding.value ?: 0


        mImageAdapter = ImageAdapter(arrayListOf())
        fuli_viewpager.adapter = mImageAdapter

        reverseNav()
        mImageAdapter?.onImageClick = {
            reverseNav()
        }

        fuli_viewpager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                fuli_title.text = fuliModel.getFuliData().value?.get(position)?.desc
                if (position == fuliModel.getFuliData().value?.size?.minus(2) ?: 0) {
                    fuliModel.fetch(++page)
                }
            }
        })
        fuli_title.text = "Loading"
    }

    private fun reverseNav() {
        mNavVisible.yes {
            fuli_titlerbar.visibility = View.VISIBLE
        } otherwise {
            fuli_titlerbar.visibility = View.GONE
        }
        mNavVisible = !mNavVisible
    }

    companion object {
        private var instance: FuLiFragment? = null
        fun newInstance(): FuLiFragment {
            if (instance == null) {
                instance = FuLiFragment()
            }
            return instance!!
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_fuli
    }
}