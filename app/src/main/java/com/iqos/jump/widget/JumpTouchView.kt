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
class JumpTouchView @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(ctx, attrs, defStyleAttr) {
    /**
     * 点1
     */
    private var mPoint1: Point? = null
    /**
     * 点2
     */
    private var mPoint2: Point? = null
    /**
     * 画笔
     */
    private val mPaint = Paint()
    /**
     * 是否是第一个坐标
     */
    private var mFirst = false
    /**
     * 是否处于按下
     */
    private var mPressed = false
    /**
     * 弹跳系数
     */
    private var mRatio = 1.390f
    /**
     * 选取坐标完成之后的回调
     * 通过距离计算时间、最终要执行的命令
     */
    private var mDistanceCallback: ((cmd: String) -> Unit)? = null

    init {
        val sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE)
        mRatio = sp.getFloat("ratio", 1.390f)
        setOnTouchListener(MyTouchListener())
        mPaint.color = Color.RED
        mPaint.textSize = 50f
        mPaint.strokeWidth = 3f
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
                    if (null == mPoint1) {
                        mPoint1 = Point(event.x, event.y)
                        mFirst = true
                    } else {
                        mPoint2 = Point(event.x, event.y)
                        mFirst = false
                    }
                    mPressed = true
                    invalidate()
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mFirst) {
                        mPoint1!!.x = event.x
                        mPoint1!!.y = event.y
                    } else {
                        mPoint2!!.x = event.x
                        mPoint2!!.y = event.y
                    }
                    invalidate()
                    return false
                }
                MotionEvent.ACTION_UP -> {
                    if (mFirst) {
                        mPoint1!!.x = event.x
                        mPoint1!!.y = event.y
                    } else {
                        mPoint2!!.x = event.x
                        mPoint2!!.y = event.y
                    }
                    v?.performClick()
                    invalidate()
                    mPressed = false
                    return false
                }
                else -> return false
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (mPressed) {
            if (null != mPoint1) this.drawLine(mPoint1!!, canvas)
            if (null != mPoint2) this.drawLine(mPoint2!!, canvas)
        } else {
            if (null != mPoint1) this.drawPoint(mPoint1!!, canvas)
            if (null != mPoint2) this.drawPoint(mPoint2!!, canvas)
            if (null != mPoint1 && null != mPoint2) {
                mPaint.color = Color.MAGENTA
                canvas?.drawLine(mPoint1!!.x, mPoint1!!.y, mPoint2!!.x, mPoint2!!.y, mPaint)
                mPaint.color = Color.BLUE
                canvas?.drawText("距离:" + calcDis(mPoint1!!, mPoint2!!).toString(), 0f, TXT_SIZE, mPaint)
                val cmd = "input swipe 1 1 0 0 " + (calcDis(mPoint1!!, mPoint2!!) * mRatio).toInt()
                canvas?.drawText("执行:$cmd", 0f, TXT_SIZE * 2 + 10f, mPaint)
                mDistanceCallback?.invoke(cmd)
                this.clearPoint()
            }
        }
    }

    fun clearPoint() {
        mPoint1 = null
        mPoint2 = null
    }

    private fun drawLine(point: Point, canvas: Canvas?) {
        mPaint.color = Color.GREEN
        canvas?.drawLine(0f, point.y, width.toFloat(), point.y, mPaint)
        canvas?.drawLine(point.x, 0f, point.x, height.toFloat(), mPaint)
    }

    private fun drawPoint(point: Point, canvas: Canvas?) {
        mPaint.color = Color.RED
        val x = point.x
        val y = point.y
        canvas?.drawCircle(x, y, 5f, mPaint)
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
    fun setDistanceCallback(distanceCallback: ((cmd: String) -> Unit)?) {
        this.mDistanceCallback = distanceCallback
    }

    companion object {
        private const val TXT_SIZE = 50f
    }
}
