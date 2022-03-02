package com.roy.jump.util;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roy on 2022/2/19
 */
public class NotificationCenter {
  public static int ACTION_PRESS_DURATION = 0x2000;

  public interface NotificationCenterDelegate {
    void handleMessage(final int action, final Object... args) throws Throwable;
  }

  private static volatile NotificationCenter instance = null;
  private final SparseArray<List<NotificationCenterDelegate>> delegatesMap = new SparseArray<>();

  private NotificationCenter() { }

  public static NotificationCenter getInstance() {
    if (null == instance) {
      synchronized (NotificationCenter.class) {
        if (null == instance) {
          instance = new NotificationCenter();
        }
      }
    }
    return instance;
  }

  public void addObserver(final int action, final NotificationCenterDelegate delegate) {
    if (null != delegate) {
      List<NotificationCenterDelegate> delegates = delegatesMap.get(action);
      if (null == delegates) {
        delegates = new ArrayList<>();
        delegatesMap.put(action, delegates);
      }
      delegates.add(delegate);
    }
  }

  public void removeObserver(final int action, final NotificationCenterDelegate delegate) {
    final List<NotificationCenterDelegate> delegates;
    if (null != delegate && null != (delegates = delegatesMap.get(action))) {
      delegates.remove(delegate);
    }
  }

  public void notify(final int action, final Object... args) {
    final List<NotificationCenterDelegate> delegates = delegatesMap.get(action);
    if (null != delegates) {
      for (final NotificationCenterDelegate delegate : delegates) {
        AndroidUtil.runOnUIThread(() -> {
          if (null != delegate) {
            try {
              delegate.handleMessage(action, args);
            } catch (Throwable e) {
              AndroidUtil.toast(e.getMessage());
            }
          }
        });
      }
    }
  }
}
