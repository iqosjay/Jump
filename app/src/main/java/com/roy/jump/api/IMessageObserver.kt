package com.roy.jump.api

/**
 * Created by Roy on 2021/9/11
 */
interface IMessageObserver {
  @Throws(Throwable::class)
  fun handleMessage(action: Int, vararg args: Any)
}