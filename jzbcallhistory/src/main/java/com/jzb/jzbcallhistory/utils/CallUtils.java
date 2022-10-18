package com.jzb.jzbcallhistory.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jzb.jzbcallhistory.activity.CallHistoryActivity;
import com.jzb.jzbcallhistory.bean.CustomBean;
import com.jzb.jzbcallhistory.config.L;
import com.jzb.jzbcallhistory.config.SendBroadcastUtil;

/**
 * create：2022/8/10 11:38
 *
 * @author ykx
 * @version 1.0
 * @Description 拨打电话以及通话状态相关工具类
 */
public class CallUtils {


    private static volatile CallUtils instance;

    public static CallUtils getInstance() {
        if (instance == null) {
            synchronized (CallUtils.class) {
                if (instance == null) {
                    instance = new CallUtils();
                }
            }
        }
        return instance;
    }

    public void callPhone(Context context, String phoneNumber) {
        try {
            if (selfCheck(context)) {
                context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
            }
        } catch (Exception e) {
            L.e(e.getMessage());
        }
    }

    /**
     * 直接拨打电话
     *
     * @param context     当前上下文对象
     * @param phoneNumber 拨打号码
     */
    public void callPhone(Context context, String phoneNumber, String nickName, String headURL, String toId, String creatorId) {
        try {
            if (selfCheck(context)) {
                if (TextUtils.isEmpty(CallHistoryActivity.callPhoneNumber)){
                    Toast.makeText(context, "号码查询中,请稍后重试", Toast.LENGTH_SHORT).show();
                    return;
                }
                context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
                CustomBean.RecordsDTO bean = new CustomBean.RecordsDTO();
                bean.setPhone(phoneNumber);
                bean.setDuration(0);
                bean.setReceiveName(nickName);
                bean.setToId(toId);
                bean.setReceiveUrl(headURL);
                bean.setCreatorId(creatorId);
                bean.setTelType("ORDINARY_CALL");
                bean.setCreateTime("刚刚");
                CallHistoryDataUtil.getInstance().setNewBean(bean);
                JSONObject extJson = new JSONObject();
                extJson.put("toId", toId);
                extJson.put("telType", "ORDINARY_CALL");
                extJson.put("phoneNumber", phoneNumber);
                extJson.put("duration", 0);
                String ext = extJson.toJSONString();
                SendBroadcastUtil.sendBroadcast(context, ext, "IM_TEL_RECORD_CREATE");
            }
        } catch (Exception e) {
            L.e(e.getMessage());
        }
    }

    /**
     * 自检
     *
     * @return 是否通过
     */
    private boolean selfCheck(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.CALL_PHONE}, 100);
            return false;
        }
        return true;
    }

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
