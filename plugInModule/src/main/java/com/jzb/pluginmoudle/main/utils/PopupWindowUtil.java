package com.jzb.pluginmoudle.main.utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jzb.pluginmoudle.R;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

/**
 * create：2022/8/12 14:14
 *
 * @author ykx
 * @version 1.0
 * @Description 搜索页面
 */
public class PopupWindowUtil {

    private static PopupWindowUtil instance;
    private PopupWindow popupWindow;

    public static PopupWindowUtil getInstance() {
        if (instance == null) {
            synchronized (PopupWindowUtil.class) {
                if (instance == null) {
                    instance = new PopupWindowUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 展示搜索页面
     */
    public void showWindow(UZModuleContext context, OnClickCallBack onClickCallBack) {
        if (popupWindow != null && popupWindow.isShowing()) return;
        Activity activity = (Activity) context.getContext();
        // 获取弹窗展示位置与大小
        int x = context.optInt("x");
        int y = context.optInt("y");
        y = CallUtils.dp2px(activity, y);
        int width = context.optInt("w");
        int height = context.optInt("h");
        height = CallUtils.dp2px(activity, height);
        String textHint = context.optString("textHint");
        popupWindow = new PopupWindow(width == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : width, height);
        // 创建内容
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.plugin_popup_window, null);
        dialogView.setFocusable(true);
        dialogView.setFocusableInTouchMode(true);
        dialogView.setOnKeyListener((arg0, arg1, arg2) -> {
            if (arg1 == KeyEvent.KEYCODE_BACK) {
                disWindow();
            }
            return false;
        });
        TextView textView = dialogView.findViewById(R.id.tv_hint);
        textView.setText(textHint);
        dialogView.findViewById(R.id.rl_dialog).setOnClickListener(v -> {
        });
        dialogView.findViewById(R.id.out_view).setOnClickListener(v -> disWindow());
        // 填充内容
        popupWindow.setContentView(dialogView);
        popupWindow.setClippingEnabled(false);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(null);
        dialogView.findViewById(R.id.tv_cancel).setOnClickListener(v -> {
            onClickCallBack.onClickCancel();
            disWindow();
        });
        dialogView.findViewById(R.id.tv_sure).setOnClickListener(v -> {
            onClickCallBack.onClickSure();
            disWindow();
        });
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.TOP, x, y);
    }

    public void showWindow(Activity context, String textHint, OnClickCallBack onClickCallBack) {
        if (popupWindow != null) {
            boolean isShow = popupWindow.isShowing();
            if (isShow) return;
        }
        if (popupWindow != null && popupWindow.isShowing()) return;
        popupWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // 创建内容
        View dialogView = LayoutInflater.from(context).inflate(R.layout.plugin_popup_window, null);
        dialogView.setFocusable(true);
        dialogView.setFocusableInTouchMode(true);
        dialogView.setOnKeyListener((arg0, arg1, arg2) -> {
            if (arg1 == KeyEvent.KEYCODE_BACK) {
                disWindow();
            }
            return false;
        });
        TextView textView = dialogView.findViewById(R.id.tv_hint);
        textView.setText(textHint);
        dialogView.findViewById(R.id.rl_dialog).setOnClickListener(v -> {
        });
        dialogView.findViewById(R.id.out_view).setOnClickListener(v -> disWindow());
        // 填充内容
        popupWindow.setContentView(dialogView);
        popupWindow.setClippingEnabled(false);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(null);
        dialogView.findViewById(R.id.tv_cancel).setOnClickListener(v -> {
            onClickCallBack.onClickCancel();
            disWindow();
        });
        dialogView.findViewById(R.id.tv_sure).setOnClickListener(v -> {
            onClickCallBack.onClickSure();
            disWindow();
        });
        popupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    public interface OnClickCallBack {
        void onClickSure();

        void onClickCancel();
    }

    public void disWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}