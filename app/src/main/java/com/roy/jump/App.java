package com.roy.jump;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by Roy on 2022/2/19
 */
public class App extends Application {
  private static App sApp = null;
  private Handler handler = null;

  @Override
  public void onCreate() {
    super.onCreate();
    handler = new Handler(Looper.myLooper());
    sApp = this;
  }

  public static App getApp() {
    return sApp;
  }

  public static Handler getHandler() {
    return sApp.handler;
  }

}
