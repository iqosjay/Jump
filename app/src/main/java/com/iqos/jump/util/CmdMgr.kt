package com.iqos.jump.util

import android.os.Handler
import android.os.Message
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.WeakReference


/**
 * Created by "iqos_jay@outlook.com" on 2018/10/24
 * Shell命令执行
 */
object CmdMgr {
    private const val ROOT_GRANTED = 0
    private const val ROOT_DENIED = 1
    private var mOutputStream: OutputStream? = null
    private lateinit var mGrantedCallback: (() -> Unit)
    private lateinit var mDeniedCallback: (() -> Unit)
    private val mHandler = RequestRootHandler(this)
    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @param pkgCodePath Apk路径 (/data/app/com.iqos.jump-1/base.apk)
     * @param granted Root授权的回调
     * @param denied  Root拒绝的回调
     * @return true表示应用获取Root权限
     */
    fun upgradeRootPermission(pkgCodePath: String, granted: (() -> Unit), denied: (() -> Unit)) {
        this.mGrantedCallback = granted
        this.mDeniedCallback = denied
        Thread {
            var process: Process? = null
            var os: DataOutputStream? = null
            try {
                val cmd = "chmod 777 $pkgCodePath"
                process = Runtime.getRuntime().exec("su")
                os = DataOutputStream(process.outputStream)
                os.writeBytes(cmd + "\n")
                os.writeBytes("exit\n")
                os.flush()
                val result = process?.waitFor()
                if (0 == result) mHandler.sendEmptyMessage(ROOT_GRANTED)
                else mHandler.sendEmptyMessage(ROOT_DENIED)
            } catch (e: Exception) {
                e.printStackTrace()
                mHandler.sendEmptyMessage(ROOT_DENIED)
            } finally {
                try {
                    os?.close()
                    process?.destroy()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    /**
     * 更新UI的Handler
     */
    private class RequestRootHandler(cmdMgr: CmdMgr) : Handler() {
        private val mWeakCmdMgr = WeakReference(cmdMgr)
        override fun handleMessage(msg: Message?) {
            val cmdMgr = mWeakCmdMgr.get() ?: return
            when (msg?.what) {
                ROOT_GRANTED -> cmdMgr.mGrantedCallback.invoke()
                ROOT_DENIED -> cmdMgr.mDeniedCallback.invoke()
                else -> super.handleMessage(msg)
            }
        }
    }

    /**
     * 执行Shell命令
     * @param cmd 要执行的命令
     */
    fun exec(cmd: String) {
        Thread {
            println(cmd)
            try {
                if (mOutputStream == null) mOutputStream = Runtime.getRuntime().exec("su").outputStream
                mOutputStream?.write(cmd.toByteArray())
                mOutputStream?.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

}