package com.iqos.jump

import android.app.Application

/**
 * Created by "iqos_jay@outlook.com" on 2018/10/25
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        sAppCtx = this
    }
    companion object {
        lateinit var sAppCtx: Application
    }
}