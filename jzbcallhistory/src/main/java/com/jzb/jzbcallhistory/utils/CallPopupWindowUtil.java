package com.jzb.jzbcallhistory.utils;

import static com.jzb.jzbcallhistory.utils.CallHistoryDataUtil.CALL_AUDIO;
import static com.jzb.jzbcallhistory.utils.CallHistoryDataUtil.CALL_PHONE;
import static com.jzb.jzbcallhistory.utils.CallHistoryDataUtil.CALL_VIDEO;

import android.app.Activity;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jzb.jzbcallhistory.R;
import com.jzb.jzbcallhistory.activity.CallHistoryActivity;
import com.jzb.jzbcallhistory.bean.CustomBean;
import com.jzb.jzbcallhistory.config.SendBroadcastUtil;

/**
 * create：2022/8/12 14:14
 *
 * @author ykx
 * @version 1.0
 * @Description 搜索页面
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
     * 展示搜索页面
     */
    public void showSearchWindow(Activity context, String userName, String headURL, String userID, String unionId) {
        if (popupWindow != null) {
            boolean isShow = popupWindow.isShowing();
            if (isShow) return;
        }
        SendBroadcastUtil.sendBroadcast(context, "{\"unionId\":" + "\"" + unionId + "\"}", "USER_ACCOUNT_GET_ACCOUNT_INFO");
        Rect outRect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        int displayHeight = outRect.bottom;
        popupWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, displayHeight);
        // 创建内容
        View dialogView = LayoutInflater.from(context).inflate(R.layout.call_popup_window, null);
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
        // 添加本地拨打记录
        JSONObject extJson = new JSONObject();
        extJson.put("callNickName", userName);
        extJson.put("corporateName", "corporateName");
        extJson.put("calleeUserid", userID);
        extJson.put("toId", unionId);
        extJson.put("telType", "VOICE");
        extJson.put("phoneNumber", CallHistoryActivity.callPhoneNumber);
        // 添加数据
        CustomBean.RecordsDTO bean = new CustomBean.RecordsDTO();
        bean.setPhone(CallHistoryActivity.callPhoneNumber);
        bean.setDuration(0);
        bean.setReceiveName(userName);
        bean.setReceiveUrl(headURL);
        bean.setToId(unionId);
        bean.setCreatorId(userID);
        bean.setCreateTime("刚刚");
        dialogView.findViewById(R.id.btn_call_audio).setOnClickListener(v -> {
            extJson.put("telType", CALL_AUDIO);
            String ext = extJson.toJSONString();
            bean.setTelType(CALL_AUDIO);
            CallHistoryDataUtil.getInstance().setNewBean(bean);
            SendBroadcastUtil.sendBroadcast(context, ext, "CALL_AUDIO");
        });
        dialogView.findViewById(R.id.btn_call_video).setOnClickListener(v -> {
            extJson.put("telType", CALL_VIDEO);
            String ext = extJson.toJSONString();
            bean.setTelType(CALL_VIDEO);
            CallHistoryDataUtil.getInstance().setNewBean(bean);
            SendBroadcastUtil.sendBroadcast(context, ext, "CALL_VIDEO");
        });
        dialogView.findViewById(R.id.btn_call_phone).setOnClickListener(v -> {
            // 添加本地拨打记录
            CallUtils.getInstance().callPhone(context, CallHistoryActivity.callPhoneNumber, userName, headURL, unionId, userID);
            DetailPopupWindowUtil.getInstance().refreshData();
            bean.setTelType(CALL_PHONE);
            CallHistoryDataUtil.getInstance().setNewBean(bean);
            CallHistoryDataUtil.getInstance().addNewCall("",0);
        });
        dialogView.findViewById(R.id.tv_cancel).setOnClickListener(v -> popupWindow.dismiss());
        // 填充内容
        popupWindow.setContentView(dialogView);
        popupWindow.setClippingEnabled(false);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(null);
        popupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.TOP, 0, 0);
    }
}
