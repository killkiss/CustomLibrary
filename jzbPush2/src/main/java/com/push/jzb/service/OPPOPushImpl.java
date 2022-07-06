package com.push.jzb.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.heytap.msp.push.callback.ICallBackResultService;
import com.push.jzb.PrivateConstants;
import com.push.jzb.utils.L;
import com.push.jzb.utils.ThirdPushTokenMgr;

public class OPPOPushImpl implements ICallBackResultService {

    @Override
    public void onRegister(int responseCode, String registerID) {
        L.e("onRegister responseCode: " + responseCode + " registerID: " + registerID);
        ThirdPushTokenMgr.getInstance().setPushTokenToTIM(PrivateConstants.OPPO_PUSH_BUZID, registerID);
    }

    @Override
    public void onUnRegister(int responseCode) {
        L.e("onUnRegister responseCode: " + responseCode);
    }

    @Override
    public void onSetPushTime(int responseCode, String s) {
        L.e("onSetPushTime responseCode: " + responseCode + " s: " + s);
    }

    @Override
    public void onGetPushStatus(int responseCode, int status) {
        L.e("onGetPushStatus responseCode: " + responseCode + " status: " + status);
    }

    @Override
    public void onGetNotificationStatus(int responseCode, int status) {
        L.e("onGetNotificationStatus responseCode: " + responseCode + " status: " + status);
    }

    @Override
    public void onError(int i, String s) {

    }

    public void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ytud_jizhibao", "默认推送", importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            channel.enableVibration(true);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
