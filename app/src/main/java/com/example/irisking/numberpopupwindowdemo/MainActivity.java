package com.example.irisking.numberpopupwindowdemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.lang.reflect.Method;

public class MainActivity extends Activity {

    private EditText editText;
    private NumberPopupWindow numberPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //点击显示数字输入框
                hideKeyBoard(editText); //隐藏系统键盘
                String numberStr = editText.getText().toString();
                final int index = editText.getSelectionStart(); //获取光标
                numberPopupWindow = new NumberPopupWindow(MainActivity.this, numberStr,
                        new NumberPopupWindow.OnInputCallback() {
                            @Override
                            public void onFinished(String numbers, int index) {
//                                Log.e("whh1115", "onFinished=" + numbers);
                                editText.setText(numbers);
                                editText.setSelection(index);//将光标移至编辑位置
                            }
                        });
                numberPopupWindow.setIndex(index); //设置初始光标位置
                numberPopupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT); //宽度铺满
                numberPopupWindow.showAtLocation(findViewById(R.id.layout_main), Gravity.BOTTOM, 0, 0); //在页面的底部显示
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (numberPopupWindow.isShowing()) {
            numberPopupWindow.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 隐藏键盘
     *
     * @param editText
     */
    private void hideKeyBoard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        try {
            Class<EditText> cls = EditText.class;
            Method setSoftInputShownOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            setSoftInputShownOnFocus.setAccessible(true);
            setSoftInputShownOnFocus.invoke(editText, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
