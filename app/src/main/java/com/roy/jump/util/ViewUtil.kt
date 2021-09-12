package com.roy.jump.util

import com.roy.jump.App

/**
 * Created by Roy on 2021/9/11
 */
object ViewUtil {

  val screenWidth: Int get() = App.appCtx.resources.displayMetrics.widthPixels
  val screenHeight: Int get() = App.appCtx.resources.displayMetrics.heightPixels
  val statusBarHt: Int get() = getStatusBarHeight()

  /**
   * 获取状态栏的高度
   * @param ctx 上下文(可以是Application的)
   * @return 状态栏的高度
   */
  private fun getStatusBarHeight(): Int {
    val resourceId = App.appCtx.resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) App.appCtx.resources.getDimensionPixelSize(resourceId) else 0
  }
}