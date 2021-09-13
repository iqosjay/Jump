package com.roy.jump.util

import android.content.Context
import com.roy.jump.App

/**
 * Created by Roy on 2021/9/11
 */
class SpUtils private constructor() {

  fun put(key: String, value: Float) {
    val sp = App.appCtx.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
    sp.edit().putFloat(key, value).apply()
  }

  fun put(key: String, value: Int) {
    val sp = App.appCtx.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
    sp.edit().putInt(key, value).apply()
  }

  fun getFloat(key: String, def: Float = 0f): Float {
    val sp = App.appCtx.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
    return sp.getFloat(key, def)
  }

  fun getInt(key: String, def: Int = 0): Int {
    val sp = App.appCtx.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
    return sp.getInt(key, def)
  }

  companion object {
    private const val FILENAME = "roy"
    val spUtils by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { SpUtils() }
  }
}