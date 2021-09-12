package com.roy.jump.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import com.roy.jump.api.IMessageObserver
import com.roy.jump.util.ACTION_PRESS
import com.roy.jump.util.MessageCenter.Companion.messageCenter
import com.roy.jump.util.ToastUtil

/**
 * Created by Roy on 2021/9/11
 */
class PerformService : AccessibilityService(), IMessageObserver {

  private val scrollPath = Path()

  init {
    val x1 = 1f
    val y1 = 1f
    val x2 = 0f
    val y2 = 0f
    scrollPath.moveTo(x1, y1)
    scrollPath.lineTo(x2, y2)
  }


  override fun onServiceConnected() {
    super.onServiceConnected()
    messageCenter.addObserver(ACTION_PRESS, this)
    ToastUtil.showToast("服务已开启.")
  }

  override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

  override fun onInterrupt() {
    ToastUtil.showToast("服务已中断.")
    messageCenter.removeObserver(ACTION_PRESS, this)
  }


  private fun longPress(ms: Long) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      val path = GestureDescription.StrokeDescription(scrollPath, 0, ms)
      val gd = GestureDescription.Builder().addStroke(path).build()
      dispatchGesture(gd, null, null)
    } else {
      ToastUtil.showToast("您的设备API低于24，无法执行全局手势.")
    }
  }

  @Throws(Throwable::class)
  override fun handleMessage(action: Int, vararg args: Any) {
    if (ACTION_PRESS == action && args.isNotEmpty()) {
      longPress(args[0] as Long)
    }
  }


}