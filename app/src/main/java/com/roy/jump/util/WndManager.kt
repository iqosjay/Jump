package com.roy.jump.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.*
import android.widget.ImageView
import com.roy.jump.App
import com.roy.jump.R
import com.roy.jump.databinding.LayoutFloatingWindowBinding
import com.roy.jump.util.MessageCenter.Companion.messageCenter
import kotlin.math.abs

/**
 * Created by Roy on 2021/9/11
 */
class WndManager {
  private val rootLayoutParams by lazy { WindowManager.LayoutParams() }
  private val iconLayoutParams by lazy { WindowManager.LayoutParams() }
  private val windowManager by lazy { App.appCtx.getSystemService(Context.WINDOW_SERVICE) as WindowManager }

  private var initialized = false
  private var binding: LayoutFloatingWindowBinding? = null
  private var rootView: View? = null
  private var ivSmallIcon: ImageView? = null
  private var xView = 0f
  private var yView = 0f
  private var currentXScreen = 0f
  private var currentYScreen = 0f
  private var pressRowX = 0f
  private var pressRowY = 0f
  private var lastTime: Long? = null


  fun showWindow() {
    this.initData()
    if (!isShown) {
      isShown = true
      try {
        windowManager.removeView(ivSmallIcon)
      } catch (ignored: Throwable) {
      }
      windowManager.addView(rootView, rootLayoutParams)
    }
  }

  private fun initData() {
    if (!initialized) {
      initIconLp()
      initRootLp()
      initRootView()
      initSmallIcon()
      initialized = true
    }
  }

  private fun hideWindow() {
    if (isShown) {
      windowManager.removeView(rootView)
      isShown = false
    }
  }

  /**
   * 可拖动的图标初始化
   */
  @SuppressLint("ClickableViewAccessibility")
  private fun initSmallIcon() {
    ivSmallIcon = ImageView(App.appCtx).also {
      it.scaleType = ImageView.ScaleType.FIT_XY
      it.setImageResource(R.mipmap.ic_launcher_round)
      it.setOnTouchListener(DrugListener())
    }
  }

  /**
   * 可拖动的图标的LayoutParams初始化
   */
  private fun initIconLp() {
    val flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    iconLayoutParams.let {
      it.type = WINDOW_TYPE
      it.flags = flags
      it.format = PixelFormat.TRANSLUCENT
      it.width = AndroidUtil.dp(50f).toInt()
      it.height = AndroidUtil.dp(50f).toInt()
      it.gravity = Gravity.START or Gravity.TOP
      it.x = currentXScreen.toInt()
      it.y = currentYScreen.toInt()
    }
  }

  /**
   * 根布局(大的弹窗)初始化
   */
  @SuppressLint("InflateParams")
  private fun initRootView() {
    rootView = LayoutInflater.from(App.appCtx).inflate(R.layout.layout_floating_window, null)?.also {
      binding = LayoutFloatingWindowBinding.bind(it)
    }
    binding?.let { bd ->
      bd.touchView.pressTimeCallback = {
        lastTime = it
        messageCenter.postEvent(ACTION_PRESS, it)
      }
      bd.buttonContinue.setOnClickListener { lastTime?.let { messageCenter.postEvent(ACTION_PRESS, it) } }
      bd.buttonClear.setOnClickListener {
        bd.touchView.clearPoint()
        bd.touchView.invalidate()
      }
      bd.ivExpand.setOnClickListener {
        this.hideWindow()
        this.showIcon()
      }
    }

  }

  /**
   * 根布局LayoutParams初始化
   */
  private fun initRootLp() {
    rootLayoutParams.let {
      val flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
      it.type = WINDOW_TYPE
      it.flags = flags
      it.format = PixelFormat.TRANSLUCENT
      it.width = ViewUtil.screenWidth
      it.height = ViewUtil.screenHeight * 3 / 4 - ViewUtil.statusBarHt
      it.gravity = Gravity.CENTER
    }
  }

  /**
   * 显示可拖动的图标
   */
  private fun showIcon() {
    windowManager.let {
      it.removeView(rootView)
      it.addView(ivSmallIcon, iconLayoutParams)
    }
  }

  /**
   * 更新拖动的图标的位置
   */
  private fun updateIconPosition() {
    iconLayoutParams.let {
      it.x = (currentXScreen - xView).toInt()
      it.y = (currentYScreen - yView).toInt()
      windowManager.updateViewLayout(ivSmallIcon, it)
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