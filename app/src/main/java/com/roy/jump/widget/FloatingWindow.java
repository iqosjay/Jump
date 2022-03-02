package com.roy.jump.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.roy.jump.R;
import com.roy.jump.util.AndroidUtil;
import com.roy.jump.util.NotificationCenter;

/**
 * Created by Roy on 2022/2/19
 */
@SuppressLint("StaticFieldLeak")
public class FloatingWindow implements View.OnTouchListener {
  private static final int CONTENT_HEIGHT = AndroidUtil.getScreenHeight() * 3 / 4;
  private static volatile FloatingWindow instance = null;
  private final WindowManager windowManager;
  private boolean isShown = false;
  private boolean isMaximize = false;
  private boolean isMinimize = false;
  private ViewGroup contentView = null;
  private WindowManager.LayoutParams contentLayoutParams = null;
  private WindowManager.LayoutParams dragBallLayoutParams = null;
  private ImageView dragBall = null;
  private float x = 0f;
  private float y = 0f;
  private float currentX = 0f;
  private float currentY = 0f;
  private float pressRawX = 0f;
  private float pressRawY = 0f;
  private long lastDuration = 0;


  private FloatingWindow() {
    final Context context = AndroidUtil.getApplicationContext();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      windowManager = context.getSystemService(WindowManager.class);
    } else {
      windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }
  }

  public static FloatingWindow getInstance() {
    if (null == instance) {
      synchronized (FloatingWindow.class) {
        if (null == instance) {
          instance = new FloatingWindow();
        }
      }
    }
    return instance;
  }

  public void show() {
    if (isShown) return;
    isShown = true;
    maximize();
  }

  private void hide() {
    if (!isShown) return;
    isShown = false;
    isMinimize = false;
    isMaximize = false;
    windowManager.removeView(contentView);
  }

  public void minimize() {
    if (isMinimize) {
      return;
    }
    isMinimize = true;
    if (null == dragBall) {
      final int dp4 = AndroidUtil.dp(4);
      dragBall = new ImageView(AndroidUtil.getApplicationContext());
      dragBall.setImageResource(R.drawable.ic_add);
      dragBall.setOnTouchListener(this);
      dragBall.setScaleType(ImageView.ScaleType.FIT_CENTER);
      dragBall.setElevation(AndroidUtil.dp(4));
      dragBall.setPadding(dp4, dp4, dp4, dp4);
      dragBall.setBackground(AndroidUtil.makeShape(0xfff5a623, 28, 0, 0));
    }
    if (null == dragBallLayoutParams) {
      final int ballSize = AndroidUtil.dp(56f);
      dragBallLayoutParams = new WindowManager.LayoutParams();
      dragBallLayoutParams.type = getWindowType();
      dragBallLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
      dragBallLayoutParams.format = PixelFormat.TRANSLUCENT;
      dragBallLayoutParams.width = ballSize;
      dragBallLayoutParams.height = ballSize;
      dragBallLayoutParams.gravity = Gravity.START | Gravity.TOP;
      dragBallLayoutParams.x = AndroidUtil.getScreenWidth() - ballSize;
      dragBallLayoutParams.y = CONTENT_HEIGHT;
    }
    if (isMaximize) {
      windowManager.removeView(contentView);
      isMaximize = false;
    }
    windowManager.addView(dragBall, dragBallLayoutParams);
  }

  public void maximize() {
    if (isMaximize) {
      return;
    }
    isMaximize = true;
    final Context context = AndroidUtil.getApplicationContext();
    if (null == contentView) {
      contentView = new RelativeLayout(context);
      final View background = new View(context);
      background.setBackgroundColor(0x22000000);
      final TouchView touchView = new TouchView(context);
      final LinearLayout linearLayout = new LinearLayout(context);
      final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
      layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
      linearLayout.setPadding(0, AndroidUtil.dp(8), 0, AndroidUtil.dp(8));
      layoutParams.setMargins(AndroidUtil.dp(16), 0, AndroidUtil.dp(16), 0);
      linearLayout.addView(createButton("关闭", Color.RED, v -> hide()));
      linearLayout.addView(createButton("收起", 0xfff5a623, v -> minimize()));
      linearLayout.addView(createButton("清除", AndroidUtil.getColor(R.color.teal_200), v -> {
        touchView.cleanPoint();
        touchView.invalidate();
        lastDuration = 0;
      }));
      linearLayout.addView(createButton("继续", AndroidUtil.getColor(R.color.teal_200), v -> {
        if (0 != lastDuration) {
          NotificationCenter.getInstance().notify(NotificationCenter.ACTION_PRESS_DURATION, lastDuration);
        }
      }));
      touchView.setDelegate(duration -> {
        lastDuration = duration;
        NotificationCenter.getInstance().notify(NotificationCenter.ACTION_PRESS_DURATION, duration);
      });
      contentView.addView(background, -1, -1);
      contentView.addView(touchView, -1, -1);
      contentView.addView(linearLayout, layoutParams);
    }
    if (null == contentLayoutParams) {
      contentLayoutParams = new WindowManager.LayoutParams();
      contentLayoutParams.type = getWindowType();
      contentLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
      contentLayoutParams.width = AndroidUtil.getScreenWidth();
      contentLayoutParams.height = CONTENT_HEIGHT;
      contentLayoutParams.format = PixelFormat.TRANSLUCENT;
      contentLayoutParams.gravity = Gravity.CENTER;
    }
    if (isMinimize) {
      windowManager.removeView(dragBall);
      isMinimize = false;
    }
    windowManager.addView(contentView, contentLayoutParams);
  }

  private void updateDragBallPosition() {
    if (null != dragBallLayoutParams) {
      dragBallLayoutParams.x = (int) (currentX - x);
      dragBallLayoutParams.y = (int) (currentY - y);
      windowManager.updateViewLayout(dragBall, dragBallLayoutParams);
    }
  }

  private static Button createButton(final String text,
                                     final int color,
                                     final View.OnClickListener listener) {
    final Button button = new Button(AndroidUtil.getApplicationContext());
    final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1f);
    layoutParams.setMargins(AndroidUtil.dp(4), 0, AndroidUtil.dp(4), 0);
    button.setText(text);
    button.setTextColor(Color.WHITE);
    button.setLayoutParams(layoutParams);
    button.setAllCaps(false);
    button.setSingleLine(true);
    button.setEllipsize(TextUtils.TruncateAt.END);
    button.setTextSize(16);
    button.setOnClickListener(listener);
    button.setBackground(AndroidUtil.makeShape(color, 4, 0, 0));
    return button;
  }

  private static int getWindowType() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
    } else {
      //noinspection deprecation
      return WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    }
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        x = event.getX();
        y = event.getY();
        currentX = pressRawX = event.getRawX();
        currentY = pressRawY = event.getRawY() - AndroidUtil.getStatusBarHeight();
        return true;
      case MotionEvent.ACTION_MOVE:
        currentX = event.getRawX();
        currentY = event.getRawY() - AndroidUtil.getStatusBarHeight();
        updateDragBallPosition();
        return true;
      case MotionEvent.ACTION_UP:
        if (Math.abs(currentX - pressRawX) < 8 && Math.abs(currentY - pressRawY) < 8) {
          maximize();
          v.performClick();
          return true;
        }
        return false;
    }
    return false;
  }
}
