package com.roy.jump;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.roy.jump.util.AndroidUtil;
import com.roy.jump.util.DataStorage;
import com.roy.jump.widget.FloatingWindow;

public class HomeActivity extends Activity {
  private static final int REQUEST_CODE_FLOATING_WINDOW = 0x1000;
  private ContentView contentView = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (null == contentView) {
      contentView = new ContentView(this);
    }
    setContentView(contentView);
  }

  private static void launchService(final HomeActivity homeActivity) {
    if (HelperService.isServiceEnabled()) {
      showFloatingWindow(homeActivity);
    } else {
      AndroidUtil.toast("请在“已安装服务”列表中激活 “" + AndroidUtil.getString(R.string.app_name) + "”");
      homeActivity.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }
  }

  private static void showFloatingWindow(final HomeActivity homeActivity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (!Settings.canDrawOverlays(AndroidUtil.getApplicationContext())) {
        final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + AndroidUtil.getPackageName()));
        homeActivity.startActivityForResult(intent, REQUEST_CODE_FLOATING_WINDOW);
        AndroidUtil.toast("请在列表中打开 “" + AndroidUtil.getString(R.string.app_name) + "” 的悬浮窗权限");
        return;
      }
    }
    FloatingWindow.getInstance().show();
  }

  private static class ContentView extends RelativeLayout {
    private static final int ID_OFFSET = 0x1000;
    private final HomeActivity homeActivity;

    private ContentView(HomeActivity context) {
      super(context);
      homeActivity = context;

      LinearLayout linearLayout = new LinearLayout(context);
      LayoutParams layoutParams = createLayoutParams();
      linearLayout.setId(ID_OFFSET);
      linearLayout.setGravity(Gravity.CENTER_VERTICAL);
      layoutParams.addRule(ALIGN_PARENT_TOP);
      linearLayout.setLayoutParams(layoutParams);
      EditText editText = createEditText(context);
      editText.setInputType(InputType.TYPE_CLASS_NUMBER);
      editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
      editText.setSingleLine(true);
      editText.setHint("输入 y 轴偏移量");
      editText.setText(String.valueOf(DataStorage.getOffset()));
      editText.addTextChangedListener(new TextViewTextWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
          if (null != editable) {
            DataStorage.saveOffset(text2int32(editable.toString()));
          }
        }
      });
      linearLayout.addView(createLabel(context, "y 轴偏移量"));
      linearLayout.addView(editText);
      addView(linearLayout);

      linearLayout = new LinearLayout(context);
      layoutParams = createLayoutParams();
      linearLayout.setGravity(Gravity.CENTER_VERTICAL);
      layoutParams.addRule(BELOW, ID_OFFSET);
      linearLayout.setLayoutParams(layoutParams);
      editText = createEditText(context);
      editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
      editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
      editText.setSingleLine(true);
      editText.setHint("输入弹跳系数");
      editText.setText(String.valueOf(DataStorage.getRatio()));
      editText.addTextChangedListener(new TextViewTextWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
          if (null != editable) {
            DataStorage.saveRatio(text2float(editable.toString()));
          }
        }
      });
      linearLayout.addView(createLabel(context, "弹跳系数"));
      linearLayout.addView(editText);
      addView(linearLayout);

      layoutParams = createLayoutParams();
      layoutParams.addRule(ALIGN_PARENT_BOTTOM);
      final Button button = new Button(context);
      button.setTextColor(Color.WHITE);
      button.setBackground(AndroidUtil.makeShape(0xfff5a623, 4, 0, 0));
      button.setAllCaps(false);
      button.setText(AndroidUtil.getString(R.string.open));
      button.setOnClickListener(view -> launchService(homeActivity));
      button.setLayoutParams(layoutParams);
      addView(button);
    }

    private static TextView createLabel(final Context context, final String text) {
      final TextView textView = new TextView(context);
      textView.setText(text);
      textView.setTextSize(16f);
      textView.setTextColor(Color.DKGRAY);
      textView.setGravity(Gravity.START);
      LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1f);
      textView.setLayoutParams(layoutParams);
      return textView;
    }

    private static EditText createEditText(final Context context) {
      final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 3f);
      final EditText editText = new EditText(context);
      editText.setTextColor(Color.BLACK);
      editText.setTextSize(18);
      editText.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
      editText.setPadding(AndroidUtil.dp(8), AndroidUtil.dp(8), AndroidUtil.dp(8), AndroidUtil.dp(8));
      layoutParams.setMargins(AndroidUtil.dp(8), 0, 0, 0);
      editText.setBackground(AndroidUtil.makeShape(0, 4, Color.LTGRAY, 1));
      editText.setLayoutParams(layoutParams);
      return editText;
    }

    private static LayoutParams createLayoutParams() {
      LayoutParams layoutParams = new LayoutParams(-1, -2);
      layoutParams.setMargins(AndroidUtil.dp(16), AndroidUtil.dp(8), AndroidUtil.dp(16), AndroidUtil.dp(8));
      return layoutParams;
    }

    private static int text2int32(final String text) {
      if (TextUtils.isEmpty(text)) {
        return 0;
      }
      try {
        return Integer.parseInt(text);
      } catch (Throwable ignored) {
        return 0;
      }
    }

    private static float text2float(final String text) {
      if (TextUtils.isEmpty(text)) {
        return 1f;
      }
      try {
        return Float.parseFloat(text);
      } catch (Throwable ignored) {
        return 1f;
      }
    }

    private static abstract class TextViewTextWatcher implements TextWatcher {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
    }


  }

}