package com.iqos.jump.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by "iqos_jay@outlook.com" on 2018/10/25
 * 触摸View
 */
class JumpTouchView @JvmOverloads constructor(private val ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(ctx, attrs, defStyleAttr) {
    private var mTouchX = -1f
    private var mTouchY = -1f
    private var mPressDownMillions = -1L
    /**
     * 点1
     */
    private var mPoint1: Point? = null
    /**
     * 点2
     */
    private var mPoint2: Point? = null
    private val mPaint = Paint()
    private var mCanvas: Canvas? = null
    /**
     * 选取坐标完成之后的回调
     * 传入的参数distance是这两个坐标的距离
     */
    private var mDistanceCallback: ((distance: Int) -> Unit)? = null

    init {
        setOnTouchListener(MyTouchListener())
        mPaint.color = Color.RED
    }

    private inner class MyTouchListener : View.OnTouchListener {
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
                    mTouchX = event.x
                    mTouchY = event.y
                    mPressDownMillions = System.currentTimeMillis()
                    return true
                }
                MotionEvent.ACTION_MOVE -> return false
                MotionEvent.ACTION_UP -> {
                    val leaveX = event.x
                    val leaveY = event.y
                    val now = System.currentTimeMillis()
                    if (Math.abs(mTouchX - leaveX) < MAX_DIS_PX && Math.abs(mTouchY - leaveY) < MAX_DIS_PX && Math.abs(mPressDownMillions - now) < MAX_MILLIONS) {
                        //可以视为点击
                        v?.performClick()
                        if (null == mPoint1) {
                            mPoint1 = Point(mTouchX, mTouchY)
                        } else {
                            mPoint2 = Point(mTouchX, mTouchY)
                            val distance = calcDis(mPoint1!!, mPoint2!!)
                            mDistanceCallback?.invoke(distance)
                            mPoint1 = null
                            mPoint2 = null
                        }
                    } else {
                        mPoint1 = null
                    }
                    return false
                }
                else -> return false
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        this.mCanvas = canvas
    }

    /**
     * 屏幕坐标对象
     */
    data class Point(var x: Float, var y: Float)

    /**
     * 计算两点距离
     * @param point1 点1
     * @param point2 点2
     * @return 两点的距离
     */
    private fun calcDis(point1: Point, point2: Point): Int {
        val xd = point1.x - point2.x
        val yd = point1.y - point2.y
        return Math.sqrt(Math.pow(xd.toDouble(), 2.0) + Math.pow(yd.toDouble(), 2.0)).toInt()
    }

    /**
     * 绑定回调接口
     */
    fun setDistanceCallback(distanceCallback: ((distance: Int) -> Unit)?) {
        this.mDistanceCallback = distanceCallback
    }

    companion object {
        private const val MAX_DIS_PX = 10
        private const val MAX_MILLIONS = 500
    }
}
