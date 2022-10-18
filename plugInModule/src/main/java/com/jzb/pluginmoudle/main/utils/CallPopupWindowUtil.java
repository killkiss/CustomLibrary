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
import com.jzb.pluginmoudle.main.widget.DialView;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

/**
 * create：2022/8/12 14:14
 *
 * @author ykx
 * @version 1.0
 * @Description 通话功能菜单
 */
public class CallPopupWindowUtil {

    private static CallPopupWindowUtil instance;
    private PopupWindow popupWindow;

    public static CallPopupWindowUtil getInstance() {
        if (instance == null) {
            synchronized (CallPopupWindowUtil.class) {
                if (instance == null) {
                    instance = new CallPopupWindowUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 展示通话功能菜单
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
        String userName = context.optString("nickName");
        popupWindow = new PopupWindow(width == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : width, height);
        // 创建内容
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.call_popup_window, null);
        dialogView.setFocusable(true);
        dialogView.setFocusableInTouchMode(true);
        dialogView.setOnKeyListener((arg0, arg1, arg2) -> {
            if (arg1 == KeyEvent.KEYCODE_BACK) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
            return false;
        });
        TextView tvName = dialogView.findViewById(R.id.tv_name);
        tvName.setText("呼叫  " + userName);
        dialogView.findViewById(R.id.out_view).setOnClickListener(v -> popupWindow.dismiss());
        dialogView.findViewById(R.id.btn_call_audio).setOnClickListener(v -> callPhoneBack.success("call_audio"));
        dialogView.findViewById(R.id.btn_call_video).setOnClickListener(v -> callPhoneBack.success("call_video"));
        dialogView.findViewById(R.id.btn_call_phone).setOnClickListener(v -> callPhoneBack.success("call_phone"));
        dialogView.findViewById(R.id.tv_cancel).setOnClickListener(v -> popupWindow.dismiss());
        // 填充内容
        popupWindow.setContentView(dialogView);
        popupWindow.setClippingEnabled(false);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.TOP, x, y);
    }

    public void disWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}
