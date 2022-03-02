package com.roy.jump.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

import com.roy.jump.util.AndroidUtil;
import com.roy.jump.util.DataStorage;
import com.roy.jump.util.MathUtil;

import java.util.Locale;

/**
 * Created by Roy on 2022/2/19
 */
public class TouchView extends View implements View.OnTouchListener {
  private static final int TEXT_SIZE = AndroidUtil.sp(16);
  private static final int TEXT_COLOR = Color.GREEN;
  private static final int LINE_COLOR = Color.WHITE;
  private static final int POINT_COLOR = Color.WHITE;
  private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Point point1 = null;
  private Point point2 = null;
  private boolean isFirst = false;
  private boolean isPress = false;
  private TouchViewDelegate delegate = null;


  public TouchView(Context context) {
    super(context);
    paint.setStrokeWidth(AndroidUtil.dp(1));
    paint.setTextSize(TEXT_SIZE);
    setOnTouchListener(this);
    setBackgroundColor(0x33000000);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    final float ratio = DataStorage.getRatio();
    if (null != point1) {
      drawLine(canvas, point1);
      drawPoint(canvas, point1);
    }
    if (null != point2) {
      drawLine(canvas, point2);
      drawPoint(canvas, point2);
    }
    if (isPress || null == point1 || null == point2) {
      return;
    }
    paint.setColor(LINE_COLOR);
    canvas.drawLine(point1.x, point1.y, point2.x, point2.y, paint);
    final Paint.FontMetrics fm = paint.getFontMetrics();
    final float textHeight = fm.bottom - fm.top;
    final int dp16 = AndroidUtil.dp(16);
    final double distance = (MathUtil.calculateDistance(point1, point2));
    final int duration = (int) (distance * ratio);
    paint.setColor(TEXT_COLOR);
    float y = textHeight + AndroidUtil.dp(8);
    canvas.drawText(String.format(Locale.CHINA, "弹跳系数：%.2f", ratio), dp16, y, paint);
    y += textHeight;
    canvas.drawText(String.format(Locale.CHINA, "起始坐标：(%1$d, %2$d)", point1.x, point1.y), dp16, y, paint);
    y += textHeight;
    canvas.drawText(String.format(Locale.CHINA, "结束坐标：(%1$d, %2$d)", point2.x, point2.y), dp16, y, paint);
    y += textHeight;
    canvas.drawText(String.format(Locale.CHINA, "勾股定理：%.2f", distance), dp16, y, paint);
    y += textHeight;
    canvas.drawText(String.format(Locale.CHINA, "长按时间：%.2f × %.2f ≈ %d", distance, ratio, duration), dp16, y, paint);
    if (null != delegate) {
      delegate.invoke(duration);
    }
    cleanPoint();
  }

  public void cleanPoint() {
    point1 = null;
    point2 = null;
  }

  private void drawLine(final Canvas canvas, final Point point) {
    paint.setColor(LINE_COLOR);
    canvas.drawLine(0f, point.y, getWidth(), point.y, paint);
    canvas.drawLine(point.x, 0f, point.x, getHeight(), paint);
  }

  private void drawPoint(final Canvas canvas, final Point point) {
    paint.setColor(POINT_COLOR);
    canvas.drawCircle(point.x, point.y, 4f, paint);
    paint.setColor(TEXT_COLOR);
    canvas.drawText(String.format(Locale.CHINA, "(%1$d, %2$d)", point.x, point.y), point.x + 4f, point.y, paint);
  }

  private void setPoint1(final MotionEvent event, final int offset) {
    final int x = (int) event.getX();
    final int y = (int) event.getY();
    if (null == point1) {
      point1 = new Point(x, y);
    } else {
      point1.x = x;
      point1.y = y - offset;
    }
  }

  private void setPoint2(final MotionEvent event, final int offset) {
    final int x = (int) event.getX();
    final int y = (int) event.getY();
    if (null == point2) {
      point2 = new Point(x, y);
    } else {
      point2.x = x;
      point2.y = y - offset;
    }
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    final int offset = DataStorage.getOffset();
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        if (null == point1) {
          setPoint1(event, offset);
          isFirst = true;
        } else {
          setPoint2(event, offset);
          isFirst = false;
        }
        isPress = true;
        invalidate();
        return true;
      case MotionEvent.ACTION_MOVE:
        if (isFirst) {
          setPoint1(event, offset);
        } else {
          setPoint2(event, offset);
        }
        invalidate();
        return false;
      case MotionEvent.ACTION_UP:
        if (isFirst) {
          setPoint1(event, offset);
        } else {
          setPoint2(event, offset);
        }
        isPress = false;
        invalidate();
        return false;
    }
    return false;
  }

  public void setDelegate(TouchViewDelegate delegate) {
    this.delegate = delegate;
  }

  public interface TouchViewDelegate {
    void invoke(long duration);
  }

}
