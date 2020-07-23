package com.xuantang.awesomegank.adapter
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.aminography.redirectglide.GlideApp
import com.xuantang.awesomegank.App

class ImageAdapter(private var data: List<String>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    inner class ImageViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)


    fun setData(data: List<String>) {
        this.data = data
    }

    var onImageClick: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val imageView = ImageView(parent.context)
        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setOnClickListener {
            onImageClick?.invoke()
        }
        return ImageViewHolder(imageView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        GlideApp.with(App.INSTANCE)
            .load(data[position])
            .into(holder.imageView)
    }
}