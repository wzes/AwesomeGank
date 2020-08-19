package com.xuantang.awesomegank.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.xuantang.awesomegank.R
import com.xuantang.awesomegank.database.Collection
import com.xuantang.awesomegank.databinding.ItemCollectionAdapterBinding
import com.xuantang.awesomegank.extentions.yes


class CollectionAdapter(private val items: MutableList<Collection>, private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun setNewData(newItems: List<Collection>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemCollectionAdapterBinding>(
            inflater,
            R.layout.item_collection_adapter,
            parent,
            false
        )
        return CollectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CollectionViewHolder).bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    internal inner class CollectionViewHolder(private val binding: ItemCollectionAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Collection) {
            binding.collection = item

            (item.cover.isNotEmpty()).yes {
                Glide.with(context)
                    .load(item.cover)
                    .into(binding.imgCover)
            }
            binding.root.setOnClickListener {
                ARouter.getInstance().build("/web/")
                    .withString("web_url", item.url)
                    .withString("title", item.title)
                    .withString("cover_url", item.cover)
                    .navigation()
            }
        }
    }
}