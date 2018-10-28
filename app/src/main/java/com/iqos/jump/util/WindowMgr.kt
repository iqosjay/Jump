package com.iqos.jump.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import com.iqos.jump.App
import com.iqos.jump.R
import com.iqos.jump.widget.JumpTouchView


/**
 * Created by "iqos_jay@outlook.com" on 2018/10/24
 * 弹窗的管理类
 */
class WindowMgr {
    private var mWindowManager: WindowManager? = null
    private var mRootView: View? = null
    private lateinit var mTouchView: JumpTouchView
    private lateinit var mIvExpand: ImageView
    private lateinit var mBtnContinue: Button
    private lateinit var mBtnClear: Button
    private var mRootLp: WindowManager.LayoutParams? = null
    private var mIconLp: WindowManager.LayoutParams? = null
    private var mIvSmallIcon: ImageView? = null
    private var mXView = 0f
    private var mYView = 0f
    private var mCurrentXScreen = 0f
    private var mCurrentYScreen = 0f
    private var mPressRowX = 0f
    private var mPressRowY = 0f
    private var mLastCmd: String? = null
    /**
     * 显示大的布局
     */
    fun showRootView() {
        if (isShown) return
        isShown = true
        mWindowManager = App.sAppCtx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (null == mRootLp) initRootLp()
        if (null == mRootView) initRootView()
        if (null != mIvSmallIcon) mWindowManager!!.removeView(mIvSmallIcon)
        mWindowManager!!.addView(mRootView, mRootLp)
    }

    /**
     * 隐藏悬浮窗
     */
    private fun hidePopupWindow() {
        if (isShown) {
            mWindowManager!!.removeView(mRootView)
            isShown = false
        }
    }

    /**
     * 可拖动的图标初始化
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initSmallIcon() {
        mIvSmallIcon = ImageView(App.sAppCtx)
        mIvSmallIcon!!.scaleType = ImageView.ScaleType.CENTER_CROP
        mIvSmallIcon!!.setImageResource(R.mipmap.ic_launcher_round)
        mIvSmallIcon!!.setOnTouchListener(DrugListener())
    }

    /**
     * 可拖动的图标的LayoutParams初始化
     */
    private fun initIconLp() {
        mIconLp = WindowManager.LayoutParams()
        @Suppress("DEPRECATION")
        mIconLp!!.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        val flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        mIconLp!!.flags = flags
        mIconLp!!.format = PixelFormat.TRANSLUCENT
        mIconLp!!.width = ViewUtil.dip2px(App.sAppCtx, 50f).toInt()
        mIconLp!!.height = ViewUtil.dip2px(App.sAppCtx, 50f).toInt()
        mIconLp!!.gravity = Gravity.START or Gravity.TOP
        mIconLp!!.x = mCurrentXScreen.toInt()
        mIconLp!!.y = mCurrentYScreen.toInt()
    }

    /**
     * 根布局(大的弹窗)初始化
     */
    @SuppressLint("InflateParams")
    private fun initRootView() {
        mRootView = LayoutInflater.from(App.sAppCtx).inflate(R.layout.layout_float_window, null)
        mTouchView = mRootView!!.findViewById(R.id.float_window_jump_touch_view)
        mIvExpand = mRootView!!.findViewById(R.id.float_window_iv_expand)
        mBtnContinue = mRootView!!.findViewById(R.id.float_window_btn_continue_last)
        mBtnClear = mRootView!!.findViewById(R.id.float_window_btn_clear)
        mTouchView.setDistanceCallback { cmd ->
            mLastCmd = cmd
            CmdMgr.exec(cmd + "\n")
        }
        mBtnContinue.setOnClickListener {
            if (true == mLastCmd?.isNotEmpty()) {
                CmdMgr.exec(mLastCmd + "\n")
            }
        }
        mBtnClear.setOnClickListener {
            mTouchView.clearPoint()
            mTouchView.invalidate()
        }
        mIvExpand.setOnClickListener {
            this.hidePopupWindow()
            this.showIcon()
        }
    }

    /**
     * 根布局LayoutParams初始化
     */
    private fun initRootLp() {
        mRootLp = WindowManager.LayoutParams()
        @Suppress("DEPRECATION")
        mRootLp!!.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
//        val flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        val flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        mRootLp!!.flags = flags
        mRootLp!!.format = PixelFormat.TRANSLUCENT
        mRootLp!!.width = ViewUtil.getScreenWidth(App.sAppCtx)
        mRootLp!!.height = ViewUtil.getScreenHeight(App.sAppCtx) * 3 / 4 - ViewUtil.getStatusBarHeight(App.sAppCtx)
        mRootLp!!.gravity = Gravity.CENTER
    }

    /**
     * 显示可拖动的图标
     */
    private fun showIcon() {
        if (null == mIvSmallIcon) initSmallIcon()
        if (null == mIconLp) initIconLp()
        mWindowManager!!.removeView(mRootView)
        mWindowManager!!.addView(mIvSmallIcon, mIconLp)
    }

    /**
     * 更新拖动的图标的位置
     */
    private fun updateIconPosition() {
        mIconLp!!.x = (mCurrentXScreen - mXView).toInt()
        mIconLp!!.y = (mCurrentYScreen - mYView).toInt()
        mWindowManager!!.updateViewLayout(mIvSmallIcon, mIconLp)
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
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    mXView = event.x
                    mYView = event.y
                    mCurrentXScreen = event.rawX
                    mCurrentYScreen = event.rawY - ViewUtil.getStatusBarHeight(App.sAppCtx)
                    mPressRowX = event.rawX
                    mPressRowY = event.rawY - ViewUtil.getStatusBarHeight(App.sAppCtx)
                }
                MotionEvent.ACTION_MOVE -> {
                    mCurrentXScreen = event.rawX
                    mCurrentYScreen = event.rawY - ViewUtil.getStatusBarHeight(App.sAppCtx)
                    updateIconPosition()
                }
                MotionEvent.ACTION_UP -> {
                    if (mCurrentXScreen == mPressRowX && mCurrentYScreen == mPressRowY) {
                        v?.performClick()
                        showRootView()
                    }
                }
            }
            return false
        }
    }

    companion object {
        private var isShown: Boolean = false
    }
}
