package com.iqos.jump

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import com.iqos.jump.util.CmdMgr
import com.iqos.jump.util.WindowMgr


class MainActivity : AppCompatActivity() {
    private var mDialog: AlertDialog? = null
    private lateinit var mBtnOk: Button
    private lateinit var mBtnConfirm: Button
    private lateinit var mEtRatio: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sp = getSharedPreferences("config.xml", Context.MODE_PRIVATE)
        mBtnOk = findViewById(R.id.app_btn_start)
        mBtnConfirm = findViewById(R.id.app_btn_confirm_ratio)
        mEtRatio = findViewById(R.id.app_et_ratio)
        mEtRatio.setText(sp.getFloat("ratio", 1.390f).toString())
        mBtnOk.setOnClickListener { this.checkRoot() }
        mBtnConfirm.setOnClickListener {
            if (mEtRatio.text.toString().isNotEmpty()) {
                sp.edit().putFloat("ratio", mEtRatio.text.toString().toFloat()).apply()
            }
        }
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
                .setCancelable(false)
                .create()
    }

    /**
     * 检查是否有Root权限
     */
    private fun checkRoot() {
        CmdMgr.upgradeRootPermission(
                packageCodePath,
                granted = {
                    val wdMgr = WindowMgr()
                    wdMgr.showRootView()
                },
                denied = {
                    if (null == mDialog) initDialog()
                    Handler().post { mDialog!!.show() }
                })
    }
}
