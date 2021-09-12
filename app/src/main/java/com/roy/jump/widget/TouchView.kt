package com.roy.jump.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.roy.jump.obj.Point
import com.roy.jump.util.AndroidUtil
import com.roy.jump.util.MathUtil
import com.roy.jump.util.SpUtils.Companion.spUtils

/**
 * Created by Roy on 2021/9/11
 */
class TouchView @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(ctx, attrs, defStyleAttr) {
  /**
   * 点1
   */
  private var point1: Point? = null

  /**
   * 点2
   */
  private var point2: Point? = null

  /**
   * 画笔
   */
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

  /**
   * 是否是第一个坐标
   */
  private var isFirst = false

  /**
   * 是否处于按下
   */
  private var isPress = false

  var pressTimeCallback: ((Long) -> Unit)? = null

  init {
    setOnTouchListener(MyTouchListener())
    paint.color = Color.RED
    paint.textSize = TEXT_SZ
    paint.strokeWidth = BOLD_SZ
  }

  private inner class MyTouchListener : OnTouchListener {
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
            if (null == point1) {
              point1 = Point(it.x, it.y)
              isFirst = true
            } else {
              point2 = Point(it.x, it.y)
              isFirst = false
            }
            isPress = true
            invalidate()
            return true
          }
          MotionEvent.ACTION_MOVE -> {
            if (isFirst) {
              point1?.x = it.x
              point1?.y = it.y
            } else {
              point2?.x = it.x
              point2?.y = it.y
            }
            invalidate()
            return false
          }
          MotionEvent.ACTION_UP -> {
            if (isFirst) {
              point1?.x = it.x
              point1?.y = it.y
            } else {
              point2?.x = it.x
              point2?.y = it.y
            }
            v?.performClick()
            invalidate()
            isPress = false
            return false
          }
          else -> return false
        }
      }
      return false
    }
  }

  override fun onDraw(canvas: Canvas?) {
    val ratio = spUtils.getFloat("ratio")
    point1?.let {
      drawLine(it, canvas)
      drawPoint(it, canvas)
    }
    point2?.let {
      drawLine(it, canvas)
      drawPoint(it, canvas)
    }
    if (!isPress) {
      val p1 = point1 ?: return
      val p2 = point2 ?: return
      paint.color = Color.MAGENTA
      canvas?.drawLine(p1.x, p1.y, p2.x, p2.y, paint)
      paint.color = Color.BLUE
      val margin = AndroidUtil.dp(16f)
      val distance = MathUtil.calculateDistance(p1, p2)
      val pressMs = (distance * ratio).toInt()
      var y = TEXT_SZ
      canvas?.drawText("弹跳系数:${ratio}", margin, y, paint)
      y += TEXT_SZ + 10
      canvas?.drawText("像素距离:${distance}", margin, y, paint)
      y += TEXT_SZ + 10

      paint.color = Color.RED
      canvas?.drawText("长按时间:${distance} * $ratio = $pressMs", margin, y, paint)

      pressTimeCallback?.invoke(pressMs.toLong())
      this.clearPoint()
    }
  }

  fun clearPoint() {
    point1 = null
    point2 = null
  }

  private fun drawLine(point: Point, canvas: Canvas?) {
    paint.color = Color.GREEN
    canvas?.drawLine(0f, point.y, width.toFloat(), point.y, paint)
    canvas?.drawLine(point.x, 0f, point.x, height.toFloat(), paint)
  }

  private fun drawPoint(point: Point, canvas: Canvas?) {
    paint.color = Color.MAGENTA
    val x = point.x
    val y = point.y
    canvas?.drawCircle(x, y, 4f, paint)
  }

  companion object {
    private const val TEXT_SZ = 48f
    private const val BOLD_SZ = 2f
  }
}