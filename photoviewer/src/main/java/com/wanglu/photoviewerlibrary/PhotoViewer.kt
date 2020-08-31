package com.wanglu.photoviewerlibrary

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.wanglu.photoviewerlibrary.activity.OnLongClickListener
import com.wanglu.photoviewerlibrary.media.PreviewConfig
import java.lang.ref.WeakReference


@SuppressLint("StaticFieldLeak")
object PhotoViewer {
    const val MEDIA_KEY = "media_key"
    const val PREVIEW_CONFIG = "preview_config"
    const val INDICATOR_TYPE_DOT = "INDICATOR_TYPE_DOT"
    const val INDICATOR_TYPE_TEXT = "INDICATOR_TYPE_TEXT"


    internal var mInterface: ShowImageViewInterface? = null

    private var mCreatedInterface: OnPhotoViewerCreatedListener? = null
    private var mDestroyInterface: OnPhotoViewerDestroyListener? = null

    private var clickView: WeakReference<View>? = null //点击那一张图片时候的view
    private var longClickListener: OnLongClickListener? = null
    private var indicatorType = INDICATOR_TYPE_DOT   // 默认type为小圆点

    private var mActivity: AppCompatActivity? = null

    interface OnPhotoViewerCreatedListener {
        fun onCreated()
    }


    interface OnPhotoViewerDestroyListener {
        fun onDestroy()
    }


    interface ShowImageViewInterface {
        fun show(iv: ImageView, url: String)
    }


    fun start(fragment: androidx.fragment.app.Fragment, previewConfig: PreviewConfig) {
        val activity = fragment.activity!!
        start(activity as AppCompatActivity, previewConfig)
    }

    fun start(activity: AppCompatActivity, previewConfig: PreviewConfig) {
        // start intent
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("xuantang://preview"))
        intent.putExtra(PREVIEW_CONFIG, previewConfig)
        activity.startActivity(intent)
        activity.overridePendingTransition(0,0)
    }
}