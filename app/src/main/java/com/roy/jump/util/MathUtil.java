package com.roy.jump.util;

import android.graphics.Point;

/**
 * Created by Roy on 2022/2/19
 */
public class MathUtil {
  public static double calculateDistance(final Point point1, final Point point2) {
    if (null == point1 || null == point2) {
      return 0f;
    }
    final int dx = point1.x - point2.x;
    final int dy = point1.y - point2.y;
    return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
  }
}
