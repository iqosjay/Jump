package com.roy.jump;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Path;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

import com.roy.jump.util.AndroidUtil;
import com.roy.jump.util.NotificationCenter;

/**
 * Created by Roy on 2022/2/20
 */
public class HelperService extends AccessibilityService implements NotificationCenter.NotificationCenterDelegate {

  private final Path slidePath = new Path();

  public HelperService() {
    slidePath.moveTo(1f, 1f);
    slidePath.lineTo(0f, 0f);
  }

  @Override
  public void onAccessibilityEvent(AccessibilityEvent event) {
  }

  @Override
  protected void onServiceConnected() {
    super.onServiceConnected();
    NotificationCenter.getInstance().addObserver(NotificationCenter.ACTION_PRESS_DURATION, this);
    AndroidUtil.toast("服务开启成功");
  }

  @Override
  public void onInterrupt() {
    NotificationCenter.getInstance().removeObserver(NotificationCenter.ACTION_PRESS_DURATION, this);
    AndroidUtil.toast("服务已中断");
  }

  private void performLongPressAction(final long duration) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      final GestureDescription.StrokeDescription strokeDescription = new GestureDescription.StrokeDescription(slidePath, 0, duration);
      final GestureDescription gestureDescription = new GestureDescription.Builder().addStroke(strokeDescription).build();
      dispatchGesture(gestureDescription, null, null);
    } else {
      AndroidUtil.toast("您的设备 API 低于 24，无法执行全局手势");
    }
  }

  @Override
  public void handleMessage(int action, Object... args) {
    if (NotificationCenter.ACTION_PRESS_DURATION == action) {
      final long duration = (long) args[0];
      performLongPressAction(duration);
    }
  }

  public static boolean isServiceEnabled() {
    final Context context = AndroidUtil.getApplicationContext();
    final ComponentName expectedComponentName = new ComponentName(context, HelperService.class);
    final String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
    if (TextUtils.isEmpty(enabledServicesSetting)) {
      return false;
    }
    final TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
    colonSplitter.setString(enabledServicesSetting);
    while (colonSplitter.hasNext()) {
      final String componentNameString = colonSplitter.next();
      final ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);
      if (enabledService != null && enabledService.equals(expectedComponentName)) {
        return true;
      }
    }
    return false;
  }

}
