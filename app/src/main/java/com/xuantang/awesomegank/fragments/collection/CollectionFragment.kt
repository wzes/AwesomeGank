package com.xuantang.awesomegank.fragments.collection

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.adapter.CollectionAdapter
import com.xuantang.awesomegank.extentions.getStatusBarHeight
import com.xuantang.awesomegank.fragments.LazyFragment
import com.xuantang.awesomegank.viewmodel.CollectionViewModel
import kotlinx.android.synthetic.main.fragment_collection.*

class CollectionFragment : LazyFragment() {
    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        CollectionAdapter(arrayListOf(), requireContext())
    }

    private val collectionModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this).get(CollectionViewModel::class.java)
    }

    companion object {
        private var instance: CollectionFragment? = null
        fun newInstance(): CollectionFragment {
            if (instance == null) {
                instance =
                    CollectionFragment()
            }
            return instance!!
        }
    }

    override fun getData() {
        collectionModel.getData().observe(this, Observer {
            adapter.setNewData(it)
        })
    }

    override fun initView() {
        collection_recycle_view.adapter = adapter
        collection_recycle_view.layoutManager = LinearLayoutManager(context)
        collection_title.text = "我的收藏"
        val layoutParams = collection_title.layoutParams as AppBarLayout.LayoutParams
        layoutParams.topMargin = context?.getStatusBarHeight()!!
        collection_title.layoutParams = layoutParams
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_collection
    }
}