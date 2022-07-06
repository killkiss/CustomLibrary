package com.push.jzb.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.push.jzb.OfflinePushModule;

/**
 * create：2022/6/20 9:15
 *
 * @author ykx
 * @version 1.0
 * @Description 监听前端点击事件
 */
public class NotificationClickReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String ext = intent.getStringExtra("ext");
        OfflinePushModule.handlerMessageCallBack(new NotifyBean(ext));
    }
}
