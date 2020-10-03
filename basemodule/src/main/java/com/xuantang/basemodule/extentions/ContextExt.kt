package com.xuantang.basemodule.extentions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.view.View

/**
 * 屏幕适配
 * 设计图宽 375px
 */
private val DESIGN_WIDTH = 375

fun Context.px(value: Int): Int = (value * resources.displayMetrics.widthPixels / DESIGN_WIDTH)

fun Context.dp(value: Int): Int = (value * resources.displayMetrics.density + 0.5f).toInt()

fun Context.dp(value: Float): Int = (value * resources.displayMetrics.density + 0.5f).toInt()

//return sp dimension value in pixels
fun Context.sp(value: Int): Int = (value * resources.displayMetrics.scaledDensity + 0.5f).toInt()

fun Context.sp(value: Float): Int = (value * resources.displayMetrics.scaledDensity + 0.5f).toInt()

//converts px value into dp or sp
fun Context.px2dp(px: Int): Float = px.toFloat() / resources.displayMetrics.density

fun Context.px2sp(px: Int): Float = px.toFloat() / resources.displayMetrics.scaledDensity

//the same for the views
fun View.dp(value: Int): Int = context.dp(value)

fun View.dp(value: Float): Int = context.dp(value)
fun View.sp(value: Int): Int = context.sp(value)
fun View.sp(value: Float): Int = context.sp(value)
fun View.px2dp(px: Int): Float = context.px2dp(px)
fun View.px2sp(px: Int): Float = context.px2sp(px)


fun Context.copyToClipBoard(url: String) {
    val cm = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val text = ClipData.newPlainText("url", url)
    cm.setPrimaryClip(text)
    toast("已复制到剪贴板")
}

fun Context.getVersionName(): String {
    return try {
        packageManager.getPackageInfo(packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        ""
    }

}

fun Context.clearCache(): Boolean {
    var result = cacheDir.deleteDir()
    if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        result = result and (externalCacheDir?.deleteDir() ?: false)
    }
    return result
}
