package com.push.jzb.service;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;
import com.push.jzb.PrivateConstants;
import com.push.jzb.utils.L;
import com.push.jzb.utils.ThirdPushTokenMgr;

public class HUAWEIHmsMessageService extends HmsMessageService {

    @Override
    public void onMessageReceived(RemoteMessage message) {
        L.e("onMessageReceived message=" + message);
    }

    @Override
    public void onMessageSent(String msgId) {
        L.e("onMessageSent msgId=" + msgId);
    }

    @Override
    public void onSendError(String msgId, Exception exception) {
        L.e("onSendError msgId=" + msgId);
    }

    @Override
    public void onNewToken(String token) {
        L.e("onNewToken token=" + token);
        L.e(token + "=== HW-token");
        ThirdPushTokenMgr.getInstance().setPushTokenToTIM(PrivateConstants.HW_PUSH_BUZID, token);
    }

    @Override
    public void onTokenError(Exception exception) {
        L.e("onTokenError exception=" + exception);
    }

    @Override
    public void onMessageDelivered(String msgId, Exception exception) {
        L.e("onMessageDelivered msgId=" + msgId);
    }

}
