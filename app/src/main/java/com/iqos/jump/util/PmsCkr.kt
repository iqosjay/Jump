package com.iqos.jump.util

import android.app.AppOpsManager
import android.content.Context
import android.os.Binder

/**
 * Created by "iqos_jay@outlook.com" on 2018/10/24
 */
object PmsCkr {
    /**
     * 判断 悬浮窗口权限是否打开
     *
     * @param context
     * @return true 允许  false禁止
     */
    fun getAppOps(context: Context): Boolean {
        try {
            val obj = context.getSystemService("appops") ?: return false
            val localClass = obj.javaClass
            val arrayOfClass = arrayOfNulls<Class<*>>(3)
            arrayOfClass[0] = Integer.TYPE
            arrayOfClass[1] = Integer.TYPE
            arrayOfClass[2] = String::class.java
            val method = localClass.getMethod("checkOp", *arrayOfClass) ?: return false
            val arrayOfObject1 = arrayOfNulls<Any>(3)
            arrayOfObject1[0] = Integer.valueOf(24)
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid())
            arrayOfObject1[2] = context.packageName
            val m = (method.invoke(obj, *arrayOfObject1) as Int).toInt()
            return m == AppOpsManager.MODE_ALLOWED
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return false
    }
}
