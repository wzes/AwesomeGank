package com.xuantang.awesomegank.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.transition.Fade
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.aminography.redirectglide.GlideApp
import com.xuantang.awesomegank.App
import com.xuantang.awesomegank.R
import com.xuantang.basemodule.extentions.setStatusTransAndDarkIcon
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.android.synthetic.main.item_image_adapter.view.*

class ImageActivity : AppCompatActivity() {
    private val imageList by lazy(LazyThreadSafetyMode.NONE) {
        intent.getStringArrayListExtra(EXTRA_IMG_LIST)
    }

    private val position by lazy(LazyThreadSafetyMode.NONE) {
        intent.getIntExtra(EXTRA_POS, 0)
    }

    companion object {
        private const val EXTRA_IMG_LIST = "imageList"
        private const val EXTRA_POS = "position"
        fun newIntent(context: Context, imageList: List<String>, position: Int): Intent {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putStringArrayListExtra(EXTRA_IMG_LIST, ArrayList(imageList))
            intent.putExtra(EXTRA_POS, position)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusTransAndDarkIcon(Color.TRANSPARENT)
        setContentView(R.layout.activity_image)
        image_view_pager.adapter = ImagePagerAdapter(imageList)
        image_view_pager.currentItem = position

        window.enterTransition = Fade()
        window.exitTransition = Fade()
    }

    private inner class ImagePagerAdapter(private val imageList: List<String>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_image_adapter, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ImageViewHolder).bind(position)
        }

        override fun getItemCount(): Int {
            return imageList.size
        }

        inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(position: Int) {
                itemView.pv.transitionName = "Image $position"
                itemView.pv.setOnClickListener {
                    onBackPressed()
                }
                GlideApp.with(App.INSTANCE).load(imageList[position]).into(itemView.pv)
            }
        }
    }
}
