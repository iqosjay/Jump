package com.roy.jump

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.roy.jump.databinding.ActivityHomeBinding
import com.roy.jump.util.AndroidUtil
import com.roy.jump.util.SpUtils.Companion.spUtils
import com.roy.jump.util.ToastUtil
import com.roy.jump.util.WndManager
import com.roy.jump.widget.TouchView.Companion.DEFAULT_OFFSET_Y
import com.roy.jump.widget.TouchView.Companion.DEFAULT_RATIO


class HomeActivity : AppCompatActivity() {

  private val windowManager by lazy { WndManager() }
  private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    binding.etRatio.setText(spUtils.getFloat("ratio", DEFAULT_RATIO).toString())
    binding.etOffsetY.setText(spUtils.getInt("offsetY", DEFAULT_OFFSET_Y).toString())
    binding.buttonStart.setOnClickListener { startHelper() }
    binding.buttonSave.setOnClickListener {
      try {
        val ratio = binding.etRatio.text?.toString()?.toFloat() ?: DEFAULT_RATIO
        val offsetY = binding.etOffsetY.text?.toString()?.toInt() ?: DEFAULT_OFFSET_Y
        spUtils.put("ratio", ratio)
        spUtils.put("offsetY", offsetY)
        ToastUtil.showToast("保存成功.")
      } catch (ignored: Exception) {
        ToastUtil.showToast("保存失败.")
      }
    }
  }

  private fun startHelper() {
    if (AndroidUtil.isAccessibilityServiceEnabled()) {
      checkFloatingWindowPermission()
    } else {
      ToastUtil.showToast("请在“已安装服务”列表中激活\"${getString(R.string.app_name)}\".")
      startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }
  }

  private fun checkFloatingWindowPermission() {
    if (!Settings.canDrawOverlays(this)) {
      val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
      startActivityForResult(intent, RC_FLOATING_WND)
      ToastUtil.showToast("在列表中打开\"${getString(R.string.app_name)}\"的悬浮窗权限.")
    } else {
      showFloatingWnd()
    }
  }

  private fun showFloatingWnd() {
    windowManager.showWindow()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (RC_FLOATING_WND == requestCode) {
      if (Settings.canDrawOverlays(this)) {
        showFloatingWnd()
      } else {
        ToastUtil.showToast("授权失败")
      }
    }
  }

  companion object {
    private const val RC_FLOATING_WND = 10
  }

}