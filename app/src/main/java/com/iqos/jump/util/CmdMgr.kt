package com.iqos.jump.util

import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream


/**
 * Created by "iqos_jay@outlook.com" on 2018/10/24
 * Shell命令执行
 */
object CmdMgr {
    private var mOutputStream: OutputStream? = null
    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    fun upgradeRootPermission(pkgCodePath: String): Boolean {
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
            return 0 == result
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            try {
                os?.close()
                process?.destroy()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 执行Shell命令
     * @param cmd 要执行的命令
     */
    fun exec(cmd: String) {
        println(cmd)
        try {
            if (mOutputStream == null) mOutputStream = Runtime.getRuntime().exec("su").outputStream
            mOutputStream?.write(cmd.toByteArray())
            mOutputStream?.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}