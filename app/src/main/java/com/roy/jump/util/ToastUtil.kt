package com.roy.jump.util

import android.widget.Toast
import com.roy.jump.App

/**
 * Created by Roy on 2021/9/11
 */
object ToastUtil {
  private var toast: Toast? = null

  fun showToast(text: String) {
    if (null == toast) {
      toast = Toast.makeText(App.appCtx, text, Toast.LENGTH_SHORT)
    } else {
      toast?.setText(text)
    }
    toast?.show()
  }
}