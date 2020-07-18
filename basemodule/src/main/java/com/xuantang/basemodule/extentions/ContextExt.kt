package com.xuantang.basemodule.extentions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import com.xuantang.basemodule.utils.toast


/**
 * 粘贴到系统剪贴板
 */
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