package com.xuantang.awesomegank.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.databinding.HotItemBinding
import com.xuantang.awesomegank.model.HotResponse
import java.text.DecimalFormat

class HotAdapter(
    private val context: Context,
    private var data: List<HotResponse.HotItemModel>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun setData(data: List<HotResponse.HotItemModel>?) {
        this.data = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<HotItemBinding>(
            inflater,
            R.layout.hot_item,
            parent,
            false
        )
        return HotViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        data!![position].let {
            (holder as HotViewHolder).bind(it, position)
        }
    }

    internal inner class HotViewHolder(private val binding: HotItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(item: HotResponse.HotItemModel, position: Int) {
            binding.common = item
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                ARouter.getInstance().build("/web/")
                    .withString("web_url", "https://gank.io/post/${item._id}")
                    .withString("title", item.desc)
                    .navigation()
            }
            binding.tvRank.text = position.plus(1).toString()
            binding.tvCount.text = formatCount(item.views * 9 + item.likeCounts * 99 + item.stars * 999)
        }

        private fun formatCount(num: Int): String {
            return if (num < 10000) {
                num.toString()
            } else {
                DecimalFormat("0.0").format(num / 10000f) + "w"
            }
        }
    }
}
