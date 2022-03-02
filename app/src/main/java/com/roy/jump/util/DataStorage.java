package com.roy.jump.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Roy on 2022/2/20
 */
public class DataStorage {
  private static final String FILENAME = "roy";
  private static final String KEY_OFFSET = "a";
  private static final String KEY_RATIO = "b";

  public static int getOffset() {
    return sharedPreference().getInt(KEY_OFFSET, 0);
  }

  public static float getRatio() {
    return sharedPreference().getFloat(KEY_RATIO, 1f);
  }

  public static void saveOffset(final int offset) {
    sharedPreference().edit().putInt(KEY_OFFSET, offset).apply();
  }

  public static void saveRatio(final float ratio) {
    sharedPreference().edit().putFloat(KEY_RATIO, ratio).apply();
  }

  private static SharedPreferences sharedPreference() {
    final Context context = AndroidUtil.getApplicationContext();
    return context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
  }
}
