package com.jzb.jzbcallhistory.utils;

import static android.content.Context.VIBRATOR_SERVICE;

import android.app.Activity;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSONObject;
import com.jzb.jzbcallhistory.R;
import com.jzb.jzbcallhistory.adpter.SearchHistoryAdapter;
import com.jzb.jzbcallhistory.bean.CustomBean;
import com.jzb.jzbcallhistory.config.L;
import com.jzb.jzbcallhistory.config.SendBroadcastUtil;

import java.util.List;

/**
 * create：2022/8/12 14:14
 *
 * @author ykx
 * @version 1.0
 * @Description 搜索页面
 */
public class DeletePopupWindowUtil {

    private static DeletePopupWindowUtil instance;
    private PopupWindow popupWindow;

    public static DeletePopupWindowUtil getInstance() {
        if (instance == null) {
            synchronized (DeletePopupWindowUtil.class) {
                if (instance == null) {
                    instance = new DeletePopupWindowUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 展示搜索页面
     */
    public void showDeleteWindow(Activity context, SearchHistoryAdapter adapter, SearchHistoryAdapter adapter2,
                                 String telID,DeleteCallBackListener listener) {
        if (popupWindow != null) {
            boolean isShow = popupWindow.isShowing();
            if (isShow) return;
        }
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(new long[]{0, 50}, -1);
        }
        popupWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // 创建内容
        View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_popup_window, null);
        dialogView.setFocusable(true);
        dialogView.setFocusableInTouchMode(true);
        dialogView.setOnKeyListener((arg0, arg1, arg2) -> {
            if (arg1 == KeyEvent.KEYCODE_BACK) {
                disPopupWindow();
            }
            return false;
        });
        dialogView.findViewById(R.id.rl_dialog).setOnClickListener(v -> L.e(""));
        dialogView.findViewById(R.id.out_view).setOnClickListener(v -> disPopupWindow());
        // 填充内容
        popupWindow.setContentView(dialogView);
        popupWindow.setClippingEnabled(false);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(null);
        dialogView.findViewById(R.id.tv_cancel).setOnClickListener(v -> disPopupWindow());
        dialogView.findViewById(R.id.tv_sure).setOnClickListener(v -> {
            // 删除记录
            if (adapter != null && adapter2 != null) {
                List<CustomBean.RecordsDTO> list = adapter.getListData();
                int position = -1;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getTelId().equals(telID)) {
                        position = i;
                    }
                }
                if (position != -1) {
                    String userId = list.get(position).getCreatorId();
                    String telId = list.get(position).getTelId();
                    String toId = list.get(position).getToId();
                    list.remove(position);
                    adapter.setListData(list);
                    // 另一个列表也需要删除
                    list = adapter2.getListData();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getCreatorId().equals(userId)) {
                            list.remove(i);
                            adapter2.setListData(list);
                            break;
                        }
                    }
                    if (listener != null) {
                        listener.DeleteSuccess();
                    }
                    JSONObject extJson = new JSONObject();
                    extJson.put("telId", telId);
                    extJson.put("toId", toId);
                    SendBroadcastUtil.sendBroadcast(context, extJson.toJSONString(), "IM_TEL_RECORD_DELETE");
                }
            }
            disPopupWindow();
        });
        popupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    private void disPopupWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public interface DeleteCallBackListener {
        void DeleteSuccess();
    }
}