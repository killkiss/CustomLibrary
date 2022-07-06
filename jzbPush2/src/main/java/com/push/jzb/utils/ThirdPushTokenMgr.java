package com.push.jzb.utils;

/**
 * create：2022/5/12 9:58
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */

import android.text.TextUtils;

import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMOfflinePushConfig;

/**
 * 在 ThirdPushTokenMgr.java 中对推送的证书 ID 及设备信息进行上报操作
 */
public class ThirdPushTokenMgr {

    public static ThirdPushTokenMgr getInstance() {
        return ThirdPushTokenHolder.instance;
    }

    private static class ThirdPushTokenHolder {
        private static final ThirdPushTokenMgr instance = new ThirdPushTokenMgr();
    }


    public void setPushTokenToTIM(long businessID, String pushToken){
        if (TextUtils.isEmpty(pushToken)) {
            L.e("setPushTokenToTIM third token is empty");
            return;
        }
        L.e("V2TIMOfflinePushConfig");
        try {
            V2TIMOfflinePushConfig v2TIMOfflinePushConfig = new V2TIMOfflinePushConfig(businessID, pushToken);
            V2TIMManager.getOfflinePushManager().setOfflinePushConfig(v2TIMOfflinePushConfig, new V2TIMCallback() {
                @Override
                public void onError(int code, String desc) {
                    L.e("code = " + code + "    desc = " + desc);
                }

                @Override
                public void onSuccess() {
                    L.e("setOfflinePushToken success");
                }
            });
        } catch (NoClassDefFoundError | NullPointerException | IllegalArgumentException e) {
            L.e("error");
        }
    }
}
