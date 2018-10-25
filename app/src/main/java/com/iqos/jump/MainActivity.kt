package com.iqos.jump

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.iqos.jump.util.CmdMgr
import com.iqos.jump.util.WindowMgr


class MainActivity : AppCompatActivity() {
    private var mDialog: AlertDialog? = null
    private lateinit var mBtnOk: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBtnOk = findViewById(R.id.app_btn_start)
        mBtnOk.setOnClickListener { this.checkRoot() }
    }

    /**
     * 初始化弹窗
     */
    private fun initDialog() {
        mDialog = AlertDialog.Builder(this)
                .setTitle("没有Root权限")
                .setMessage("运行时需要执行shell命令，必须拥有root权限！")
                .setPositiveButton("确定") { _, _ -> checkRoot() }
                .setNegativeButton("退出") { _, _ -> System.exit(0) }
                .create()
    }

    /**
     * 检查是否有Root权限
     */
    private fun checkRoot() {
        val root = CmdMgr.upgradeRootPermission(packageCodePath)
        if (root) {
            val wdMgr=WindowMgr()
            wdMgr.showRootView()
        } else {
            if (null == mDialog) initDialog()
            Handler().post { mDialog!!.show() }
        }
    }
}
