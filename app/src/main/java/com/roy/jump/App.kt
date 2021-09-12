package com.roy.jump

import android.app.Application

/**
 * Created by Roy on 2021/9/11
 */
class App : Application() {
  override fun onCreate() {
    super.onCreate()
    instance = this
  }

  companion object {
    private var instance: App? = null
    val appCtx by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { instance!!.applicationContext }
  }
}