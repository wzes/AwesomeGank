package com.xuantang.basemodule

import android.app.Application
import android.content.ContextWrapper
import com.alibaba.android.arouter.launcher.ARouter


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
    }

    companion object {
        @JvmStatic
        lateinit var INSTANCE: App
    }
}

object AppContext : ContextWrapper(App.INSTANCE)