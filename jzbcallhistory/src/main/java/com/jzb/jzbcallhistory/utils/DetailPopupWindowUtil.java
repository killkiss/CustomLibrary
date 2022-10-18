package com.jzb.jzbcallhistory.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.jzb.jzbcallhistory.R;
import com.jzb.jzbcallhistory.activity.CallHistoryActivity;
import com.jzb.jzbcallhistory.adpter.SearchDetailAdapter;
import com.jzb.jzbcallhistory.bean.CustomBean;
import com.jzb.jzbcallhistory.config.SendBroadcastUtil;


/**
 * create：2022/8/12 14:14
 *
 * @author ykx
 * @version 1.0
 * @Description 搜索页面
 */
public class DetailPopupWindowUtil {

    private static DetailPopupWindowUtil instance;
    private PopupWindow popupWindow;
    private View dialogView;
    private Activity context;
    private String ext;

    public static DetailPopupWindowUtil getInstance() {
        if (instance == null) {
            synchronized (DetailPopupWindowUtil.class) {
                if (instance == null) {
                    instance = new DetailPopupWindowUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 展示搜索页面
     */
    public void showSearchWindow(Activity context, String headUrl, String nickName, String unionId, String toId) {
        if (popupWindow != null) {
            boolean isShow = popupWindow.isShowing();
            if (isShow) return;
        }
        CallHistoryActivity.callPhoneNumber = null;
        this.context = context;
        JSONObject extJson = new JSONObject();
        extJson.put("unionId", unionId);
        extJson.put("toId", toId);
        extJson.put("page", 1);
        ext = extJson.toJSONString();
        SendBroadcastUtil.sendBroadcast(context, ext, "IM_TEL_RECORD_DETAIL");
        Rect outRect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        int displayHeight = outRect.bottom;
        popupWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, displayHeight);
        // 创建内容
        dialogView = LayoutInflater.from(context).inflate(R.layout.search_detail, null);
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
        dialogView.findViewById(R.id.tv_call).setOnClickListener(v -> {
            CallHistoryActivity.isDetailPager = true;
            CallHistoryActivity.callPhoneNumber = null;
            CallPopupWindowUtil.getInstance().showSearchWindow(context, nickName, headUrl, unionId, toId);
        });
        // 添加头像
        ImageView ivHead = dialogView.findViewById(R.id.iv_img);
        TextView tvHead = dialogView.findViewById(R.id.tv_img);
        if (!TextUtils.isEmpty(headUrl)) {
            tvHead.setVisibility(View.GONE);
            // 设置图片圆角角度
            Glide.with(context).load(headUrl).into(ivHead);
        } else {
            tvHead.setVisibility(View.VISIBLE);
            String imgName = nickName;
            if (!TextUtils.isEmpty(imgName) && imgName.length() > 2) {
                imgName = imgName.substring(imgName.length() - 2);
            }
            tvHead.setText(imgName);
        }
        TextView tvName = dialogView.findViewById(R.id.tv_name);
        tvName.setText(nickName);
        // 填充内容
        popupWindow.setContentView(dialogView);
        popupWindow.setClippingEnabled(false);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(null);
        // 返回键
        dialogView.findViewById(R.id.iv_back).setOnClickListener(v -> popupWindow.dismiss());
        popupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.TOP, 0, 0);
    }

    public void setData(String data) {
        CustomBean bean = JSONObject.parseObject(data, CustomBean.class);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view);
        SearchDetailAdapter adapter = new SearchDetailAdapter(context);
        adapter.setData(bean.getRecords());
        recyclerView.setAdapter(adapter);
        handler.sendEmptyMessageDelayed(0, 100);
    }

    private final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (dialogView != null) {
                dialogView.findViewById(R.id.rl_loading).setVisibility(View.GONE);
            }
            return false;
        }
    });

    public void refreshData() {
        if (!TextUtils.isEmpty(ext) && dialogView != null) {
            dialogView.findViewById(R.id.rl_loading).setVisibility(View.VISIBLE);
            SendBroadcastUtil.sendBroadcast(context, ext, "IM_TEL_RECORD_DETAIL");
        }
    }
}