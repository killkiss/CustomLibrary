package com.jzb.jzbcallhistory.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.jzb.jzbcallhistory.R;

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
    public void showSearchWindow(Activity context) {
        if (popupWindow != null) {
            boolean isShow = popupWindow.isShowing();
            if (isShow) return;
        }
        Rect outRect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        int displayHeight = outRect.bottom;
        popupWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, displayHeight - dip2px(context, 61));
        // 创建内容
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dial_popup_window, null);
        dialogView.setFocusable(true);
        dialogView.setFocusableInTouchMode(true);
        dialogView.setOnKeyListener((arg0, arg1, arg2) -> {
            if (arg1 == KeyEvent.KEYCODE_BACK) {
            }
            return false;
        });
        dialogView.findViewById(R.id.iv_back).setOnClickListener(v -> context.finish());
        // 填充内容
        popupWindow.setContentView(dialogView);
        popupWindow.setClippingEnabled(false);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.TOP, 0, 0);
    }

    public void disWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}