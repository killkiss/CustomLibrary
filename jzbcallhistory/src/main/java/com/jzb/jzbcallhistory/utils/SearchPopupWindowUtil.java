package com.jzb.jzbcallhistory.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

import com.jzb.jzbcallhistory.R;
import com.jzb.jzbcallhistory.activity.CallHistoryActivity;
import com.jzb.jzbcallhistory.config.Constants;
import com.jzb.jzbcallhistory.widget.SearchPopupWindowView;

/**
 * create：2022/8/12 14:14
 *
 * @author ykx
 * @version 1.0
 * @Description 搜索页面
 */
public class SearchPopupWindowUtil {

    private static SearchPopupWindowUtil instance;
    private PopupWindow popupWindow;
    private SearchPopupWindowView view;
    private String nickname, headURL, unionId;
    private Activity context;

    public static SearchPopupWindowUtil getInstance() {
        if (instance == null) {
            synchronized (SearchPopupWindowUtil.class) {
                if (instance == null) {
                    instance = new SearchPopupWindowUtil();
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
        popupWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, displayHeight);
        // 创建内容
        View dialogView = LayoutInflater.from(context).inflate(R.layout.search_popup_window, null);
        view = dialogView.findViewById(R.id.search_view);
        view.setFinishCallBack((nickname, userId, headURL, unionId, phone) -> {
            if (view != null && popupWindow != null) {
                view.releaseView();
                popupWindow.dismiss();
            }
            CallHistoryActivity.isDetailPager = false;
            CallHistoryActivity.callPhoneNumber = phone;
            this.context = context;
            this.nickname = nickname;
            this.headURL = headURL;
            this.unionId = unionId;
            handler.sendEmptyMessageDelayed(0, 100);
        });
        dialogView.setFocusable(true);
        dialogView.setFocusableInTouchMode(true);
        dialogView.setOnKeyListener((arg0, arg1, arg2) -> {
            if (arg1 == KeyEvent.KEYCODE_BACK) {
                if (view != null && popupWindow != null) {
                    view.releaseView();
                    popupWindow.dismiss();
                }
            }
            return false;
        });
        // 填充内容
        popupWindow.setContentView(dialogView);
        popupWindow.setClippingEnabled(false);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.TOP, 0, 0);
        view.getImageBack().setOnClickListener(v -> {
            if (view != null && popupWindow != null) {
                view.releaseView();
                popupWindow.dismiss();
            }
        });
        new Thread(() -> {
            try {
                Thread.sleep(100);
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view.getEditText(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void setData(String data) {
        if (view != null) {
            view.setAdapterData(data);
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            CallPopupWindowUtil.getInstance().showSearchWindow(context, nickname, headURL, Constants.unionId, unionId);
        }
    };
}
