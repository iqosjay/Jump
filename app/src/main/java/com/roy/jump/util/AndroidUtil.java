package com.roy.jump.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.roy.jump.App;

/**
 * Created by Roy on 2022/2/19
 */
public class AndroidUtil {
  private static final DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
  private static final int screenWidth = dm.widthPixels;
  private static final int screenHeight = dm.heightPixels;
  private static Toast toast = null;

  public static int dp(final float dp) { return (int) (0.5f + dp * dm.density); }

  public static int sp(final float dp) { return (int) (0.5f + dp * dm.scaledDensity); }

  public static int getScreenWidth() { return screenWidth; }

  public static int getScreenHeight() { return screenHeight; }

  public static int getStatusBarHeight() {
    final Resources resources = getApplicationContext().getResources();
    final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
    return resourceId > 0 ? resources.getDimensionPixelSize(resourceId) : 0;
  }

  public static Context getApplicationContext() {
    return App.getApp().getApplicationContext();
  }

  public static String getPackageName() {
    return getApplicationContext().getPackageName();
  }

  public static int getColor(final int resId) {
    final Context context = getApplicationContext();
    return context.getResources().getColor(resId);
  }

  public static String getString(final int resId) {
    final Context context = getApplicationContext();
    return context.getResources().getString(resId);
  }

  public static void toast(final String text) {
    if (!TextUtils.isEmpty(text)) {
      if (null != toast) {
        toast.cancel();
      }
      if (null == toast) {
        toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
      } else {
        toast.setText(text);
      }
      toast.show();
    }
  }

  public static Drawable makeShape(final int solidColor,
                                   final float cornerRadius,
                                   final int strokeColor,
                                   final float strokeWidth) {
    final GradientDrawable drawable = new GradientDrawable();
    drawable.setColor(solidColor);
    drawable.setCornerRadius(dp(cornerRadius));
    drawable.setStroke(dp(strokeWidth), strokeColor);
    return drawable;
  }

  public static void runOnUIThread(final Runnable runnable) {
    runOnUIThread(runnable, 0);
  }

  public static void runOnUIThread(final Runnable runnable, final long delay) {
    final Handler handler = App.getHandler();
    if (null == handler || null == runnable) {
      return;
    }
    if (0 >= delay) {
      if (Looper.getMainLooper() == Looper.myLooper()) {
        runnable.run();
      } else {
        handler.post(runnable);
      }
    } else {
      handler.postDelayed(runnable, delay);
    }
  }

}
