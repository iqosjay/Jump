package com.roy.jump.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import com.roy.jump.App
import com.roy.jump.R
import com.roy.jump.databinding.LayoutFloatingWindowBinding
import com.roy.jump.util.MessageCenter.Companion.messageCenter
import kotlin.math.abs

/**
 * Created by Roy on 2021/9/11
 */
@SuppressLint("InflateParams")
class WndManager {

  private val windowManager by lazy { App.appCtx.getSystemService(Context.WINDOW_SERVICE) as WindowManager }
  private val rootHeight by lazy { ViewUtil.screenHeight * 3 / 4 - ViewUtil.statusBarHt }
  private val rootLayoutParams by lazy {
    WindowManager.LayoutParams().also {
      val flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
      it.type = WINDOW_TYPE
      it.flags = flags
      it.format = PixelFormat.TRANSLUCENT
      it.width = ViewUtil.screenWidth
      it.height = rootHeight
      it.gravity = Gravity.CENTER
    }
  }

  private val iconLayoutParams by lazy {
    WindowManager.LayoutParams().also {
      val flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
      val ballSz = AndroidUtil.dp(56f).toInt()
      it.type = WINDOW_TYPE
      it.flags = flags
      it.format = PixelFormat.TRANSLUCENT
      it.width = ballSz
      it.height = ballSz
      it.gravity = Gravity.START or Gravity.TOP
      it.x = ViewUtil.screenWidth - ballSz
      it.y = rootHeight
    }
  }

  private val dragBall by lazy {
    ImageButton(App.appCtx).also {
      val dp4 = AndroidUtil.dp(4f).toInt()
      it.setImageResource(R.drawable.ic_baseline_add_24)
      it.setOnTouchListener(DrugListener())
      it.setPadding(dp4, dp4, dp4, dp4)
      it.elevation = dp4.toFloat()
      it.scaleType = ImageView.ScaleType.CENTER_CROP
      it.background = AndroidUtil.createShape(AndroidUtil.dp(28f), AndroidUtil.getColor(R.color.accent2), 0, 0)
    }
  }

  private val rootView by lazy { LayoutInflater.from(App.appCtx).inflate(R.layout.layout_floating_window, null) }
  private val binding by lazy { LayoutFloatingWindowBinding.bind(rootView) }

  private var initialized = false
  private var xView = 0f
  private var yView = 0f
  private var currentXScreen = 0f
  private var currentYScreen = 0f
  private var pressRowX = 0f
  private var pressRowY = 0f
  private var lastTime: Long? = null


  fun showWindow() {
    this.bindEvents()
    if (!isShown) {
      isShown = true
      try {
        windowManager.removeView(dragBall)
      } catch (ignored: Throwable) {
      }
      windowManager.addView(rootView, rootLayoutParams)
    }
  }

  private fun hideWindow() {
    if (isShown) {
      windowManager.removeView(rootView)
      isShown = false
    }
  }


  /**
   * 根布局(大的弹窗)初始化
   */
  private fun bindEvents() {
    if (initialized) {
      return
    }
    initialized = true
    binding.touchView.pressTimeCallback = {
      lastTime = it
      messageCenter.postEvent(ACTION_PRESS, it)
    }
    binding.buttonContinue.setOnClickListener { lastTime?.let { messageCenter.postEvent(ACTION_PRESS, it) } }
    binding.buttonClear.setOnClickListener {
      binding.touchView.clearPoint()
      binding.touchView.invalidate()
    }
    binding.buttonMinimize.setOnClickListener {
      this.hideWindow()
      this.showIcon()
    }
  }

  /**
   * 显示可拖动的图标
   */
  private fun showIcon() {
    windowManager.let {
      it.removeView(rootView)
      it.addView(dragBall, iconLayoutParams)
    }
  }

  /**
   * 更新拖动的图标的位置
   */
  private fun updateIconPosition() {
    iconLayoutParams.let {
      it.x = (currentXScreen - xView).toInt()
      it.y = (currentYScreen - yView).toInt()
      windowManager.updateViewLayout(dragBall, it)
    }
  }

  /**
   * 拖动的图标的触摸事件
   */
  private inner class DrugListener : View.OnTouchListener {
    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     * the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
      event?.let {
        when (it.action) {
          MotionEvent.ACTION_DOWN -> {
            xView = it.x
            yView = it.y
            currentXScreen = it.rawX
            currentYScreen = it.rawY - ViewUtil.statusBarHt
            pressRowX = it.rawX
            pressRowY = it.rawY - ViewUtil.statusBarHt
          }
          MotionEvent.ACTION_MOVE -> {
            currentXScreen = it.rawX
            currentYScreen = it.rawY - ViewUtil.statusBarHt
            updateIconPosition()
          }
          MotionEvent.ACTION_UP -> {
            if (abs(currentXScreen - pressRowX) < 10 && abs(currentYScreen - pressRowY) < 10) {
              v?.performClick()
              showWindow()
            }
          }
        }
        return false
      }
      return false
    }
  }

  companion object {
    private var isShown = false

    private val WINDOW_TYPE = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
      @Suppress("DEPRECATION")
      WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
    }
  }
}