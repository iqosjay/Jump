package com.roy.jump.util

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import com.roy.jump.App
import com.roy.jump.R
import com.roy.jump.service.PerformService

/**
 * Created by Roy on 2021/9/11
 */
object AndroidUtil {
  private val handler = Handler(Looper.getMainLooper())
  fun runOnUIThread(runnable: Runnable) {
    runOnUIThread(0, runnable)
  }

  fun runOnUIThread(delay: Long, runnable: Runnable) {
    if (0 >= delay) {
      if (Looper.myLooper() == Looper.getMainLooper()) {
        runnable.run()
      } else {
        handler.post(runnable)
      }
    } else {
      handler.postDelayed(runnable, delay)
    }
  }

  /**
   * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
   *
   * @param dp float
   * @return int dpValue对应的px值
   */
  fun dp(dp: Float): Float {
    val scale = App.appCtx.resources.displayMetrics.density
    return (dp * scale + 0.5f)
  }

  fun createShape(radius: Float, solidColor: Int, strokeColor: Int, strokeWidth: Int): Drawable {
    val shape = GradientDrawable()
    shape.cornerRadius = radius
    shape.setStroke(strokeWidth, strokeColor)
    shape.setColor(solidColor)
    return shape
  }

  fun getColor(colorId: Int): Int {
    return App.appCtx.resources.getColor(colorId, null)
  }

  fun isAccessibilityServiceEnabled(): Boolean {
    val context = App.appCtx
    val expectedComponentName = ComponentName(context, PerformService::class.java)
    val enabledServicesSetting = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
    val colonSplitter = TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServicesSetting)
    while (colonSplitter.hasNext()) {
      val componentNameString = colonSplitter.next()
      val enabledService = ComponentName.unflattenFromString(componentNameString)
      if (enabledService != null && enabledService == expectedComponentName) {
        return true
      }
    }
    return false
  }
}