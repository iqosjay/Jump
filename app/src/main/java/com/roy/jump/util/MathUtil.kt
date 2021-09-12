package com.roy.jump.util

import com.roy.jump.obj.Point
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Created by Roy on 2021/9/11
 */
object MathUtil {

  /**
   * 计算两点距离
   * @param point1 点1
   * @param point2 点2
   * @return 两点的距离
   */
  fun calculateDistance(point1: Point, point2: Point): Int {
    val xd = point1.x - point2.x
    val yd = point1.y - point2.y
    return sqrt(xd.toDouble().pow(2.0) + yd.toDouble().pow(2.0)).toInt()
  }
}