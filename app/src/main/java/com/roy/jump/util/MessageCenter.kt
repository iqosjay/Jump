package com.roy.jump.util

import com.roy.jump.api.IMessageObserver
import java.util.*

const val ACTION_PRESS = 1

/**
 * Created by Roy on 2021/9/11
 */
class MessageCenter {
  private val observerMap = hashMapOf<Int, LinkedList<IMessageObserver>>()

  fun addObserver(action: Int, observer: IMessageObserver) {
    val observers = observerMap[action] ?: LinkedList()
    if (!observers.contains(observer)) {
      observers.add(observer)
    }
    observerMap[action] = observers
  }

  fun removeObserver(action: Int, observer: IMessageObserver) {
    observerMap[action]?.remove(observer)
  }

  fun postEvent(action: Int, vararg args: Any) {
    observerMap[action]?.forEach {
      try {
        AndroidUtil.runOnUIThread { it.handleMessage(action, *args) }
      } catch (e: Throwable) {
        AndroidUtil.runOnUIThread { ToastUtil.showToast(e.message ?: "failed while post event") }
      }
    }
  }

  companion object {
    val messageCenter by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { MessageCenter() }
  }
}