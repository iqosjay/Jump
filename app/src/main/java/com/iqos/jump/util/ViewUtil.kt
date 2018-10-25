package com.iqos.jump.util

import android.app.Application

/**
 * Created by "iqos_jay@outlook.com" on 2018/10/25
 * 视图的工具类
 */
object ViewUtil {
    /**
     * 获取屏幕的宽度
     * @param ctx 上下文(可传入Application)
     * @return 屏幕的宽度
     */
    fun getScreenWidth(ctx: Application): Int = ctx.resources.displayMetrics.widthPixels

    /**
     * 获取屏幕的高度
     * @param ctx 上下文(可传入Application)
     * @return 屏幕的高度
     */
    fun getScreenHeight(ctx: Application): Int = ctx.resources.displayMetrics.heightPixels

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context Context
     * @param dpValue float
     * @return int dpValue对应的px值
     */
    fun dip2px(context: Application, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

    /**
     * 获取状态栏的高度
     * @param ctx 上下文(可以是Application的)
     * @return 状态栏的高度
     */
    fun getStatusBarHeight(ctx: Application): Int {
        val resourceId = ctx.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) ctx.resources.getDimensionPixelSize(resourceId); else 0
    }
}