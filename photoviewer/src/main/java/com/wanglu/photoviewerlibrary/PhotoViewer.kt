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
    const val PREVIEW_CONFIG = "preview_config"
    const val INDICATOR_TYPE_DOT = "INDICATOR_TYPE_DOT"

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