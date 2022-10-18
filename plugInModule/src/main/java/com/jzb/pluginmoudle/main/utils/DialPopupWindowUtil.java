package com.jzb.pluginmoudle.main.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.jzb.pluginmoudle.main.widget.DialView;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;


/**
 * create：2022/8/12 14:14
 *
 * @author ykx
 * @version 1.0
 * @Description 搜索页面
 */
public class DialPopupWindowUtil {

    private static DialPopupWindowUtil instance;
    private PopupWindow popupWindow;

    public static DialPopupWindowUtil getInstance() {
        if (instance == null) {
            synchronized (DialPopupWindowUtil.class) {
                if (instance == null) {
                    instance = new DialPopupWindowUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 展示搜索页面
     */
    public void showSearchWindow(UZModuleContext context, DialView.CallPhoneBack callPhoneBack) {
        if (popupWindow != null && popupWindow.isShowing()) return;
        Activity activity = (Activity) context.getContext();
        // 获取弹窗展示位置与大小
        int x = context.optInt("x");
        int y = context.optInt("y");
        y = CallUtils.dp2px(activity, y);
        int width = context.optInt("w");
        int height = context.optInt("h");
        height = CallUtils.dp2px(activity, height);
        popupWindow = new PopupWindow(width == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : width, height);
        // 创建内容
        LinearLayout dialogView = new LinearLayout(activity);
        dialogView.setOrientation(LinearLayout.VERTICAL);
        dialogView.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialogView.setLayoutParams(params);
        // 创建拨号盘
        DialView dialView = new DialView(activity);
        dialView.setLayoutParams(params);
        dialView.setCallPhoneBack(callPhoneBack);
        dialogView.addView(dialView);
        // 设置焦点
        dialogView.setFocusable(true);
        dialogView.setFocusableInTouchMode(true);
        dialogView.setOnKeyListener((arg0, arg1, arg2) -> {
            if (arg1 == KeyEvent.KEYCODE_BACK) {
                callPhoneBack.success("disWindow");
            }
            return false;
        });
        // 填充内容
        popupWindow.setContentView(dialogView);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.showAtLocation(((Activity) context.getContext()).getWindow().getDecorView(), Gravity.TOP, x, y);
    }

    public void disWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}