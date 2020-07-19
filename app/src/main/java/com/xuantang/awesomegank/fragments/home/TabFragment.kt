package com.xuantang.awesomegank.fragments.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.components.ArticleRecyclerView
import com.xuantang.awesomegank.extentions.yes
import kotlinx.android.synthetic.main.fragment_tab.*


class TabFragment(val category: String) : Fragment() {
    private var isInit = false
    private var isFirstVisible = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    private fun initView() {
        home_tab_recyclerview.adapter = ArticleAdapter()
        home_tab_recyclerview.layoutManager = LinearLayoutManager(context)
    }

    private fun getData() {
        //
    }


    override fun onDestroy() {
        super.onDestroy()
        isFirstVisible = true
    }

    override fun onResume() {
        super.onResume()
        initView()
        isInit = true
        isFirstVisible.yes {
            getData()
            isFirstVisible = false
        }
    }

    inner class ArticleAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val textView = TextView(parent.context)
            textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            textView.setTextColor(Color.BLACK)
            textView.textSize = 15f
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            return ArticleViewHolder(textView)
        }

        override fun getItemCount(): Int {
            return 99
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ArticleViewHolder).textView.text = "Item $position"
        }

    }

    internal inner class ArticleViewHolder(var textView: TextView) : RecyclerView.ViewHolder(textView)
}