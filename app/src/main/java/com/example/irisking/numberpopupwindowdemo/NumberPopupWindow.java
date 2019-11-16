package com.example.irisking.numberpopupwindowdemo;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义数字键盘
 */
public class NumberPopupWindow extends PopupWindow {

    private NumberPopupWindow customNumberView;
    private int index; //EditText的光标位置
    private GridView grid_number;
    private static WeakReference<Context> contextReference;
    private StringBuffer sb = new StringBuffer();

    /**
     * @param context
     * @param numberStr 已输入的字符串
     * @param callback  输入数字的回调
     */
    public NumberPopupWindow(final Context context, String numberStr, OnInputCallback callback) {
        super(context);
        if (contextReference == null || contextReference.get() != context) {
            contextReference = new WeakReference<Context>(context);
        }
        customNumberView = this;
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_number_view, null);
        setContentView(inflate);
        setOutsideTouchable(false); //不能设为true! 否则当点击EditView时也会关闭
        //如需实现点击外部空白区域，可自行设置其他View点击时执行customNumberView.dismiss();

        grid_number = inflate.findViewById(R.id.grid_number);
        grid_number.setAdapter(new NumberGridAdapter(context, numberStr, callback));
    }

    //设置光标位置
    public void setIndex(int index) {
        this.index = index;
    }

    class NumberGridAdapter extends BaseAdapter {

        private Context context;
        private OnInputCallback callback;
        private List<String> numbers = new ArrayList<>();

        public NumberGridAdapter(Context context, String numberStr, OnInputCallback callback) {
            this.context = context;
            if (numberStr != null) sb.append(numberStr); //追加输入框已有的数字
            this.callback = callback;

            for (int i = 1; i < 10; i++) {
                numbers.add(i + "");
            }
            numbers.add("删除");
            numbers.add("0");
            numbers.add("完成");
        }

        @Override
        public int getCount() {
            return numbers.size();
        }

        @Override
        public Object getItem(int position) {
            return numbers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_number_view, null);
                viewHolder = new ViewHolder();
                viewHolder.btn_item = convertView.findViewById(R.id.btn_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.btn_item.setText(numbers.get(position));
            if (position == 9) { //删除按钮
                viewHolder.btn_item.setTextColor(Color.parseColor("#D81B60"));
                viewHolder.btn_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (sb.length() > 0 && index > 0) { //当有文字时且光标不在第一位时，可删除
                            sb.deleteCharAt(index - 1);
                            index--; //光标位置递减
                        }
//                        Log.e("whh1115", "delete=>" + sb.toString());

                        if (callback != null) {
                            callback.onFinished(sb.toString(), index);
                        }
                    }
                });

            } else if (position == 11) { //完成按钮
                viewHolder.btn_item.setTextColor(Color.parseColor("#4A90E2"));
                viewHolder.btn_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (customNumberView != null) customNumberView.dismiss();
                        else Log.e("whh1115", "customNumberView is null");
                    }
                });

            } else { //点击数字
                viewHolder.btn_item.setTextColor(Color.BLACK);
                viewHolder.btn_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView textView = (TextView) view;
                        sb.insert(index, textView.getText().toString());
                        index++; //光标位置递增
//                        Log.e("whh1115", "append=>" + sb.toString());

                        if (callback != null) {
                            callback.onFinished(sb.toString(), index);
                        }

                    }
                });
            }

            return convertView;
        }

        class ViewHolder {
            Button btn_item;
        }

    }

    /**
     * 确定按钮的回调
     */
    public interface OnInputCallback {
        void onFinished(String numbers, int index);
    }

}
