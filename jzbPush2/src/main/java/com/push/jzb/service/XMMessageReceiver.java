package com.push.jzb.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.push.jzb.PrivateConstants;
import com.push.jzb.utils.L;
import com.push.jzb.utils.ThirdPushTokenMgr;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;
import java.util.Map;

public class XMMessageReceiver extends PushMessageReceiver {

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        L.e("onReceivePassThroughMessage");
        L.e(message.getContent());
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        L.e("onNotificationMessageClicked");
        Map<String, String> map = message.getExtra();
        if (context != null && map != null) {
            L.e("跳转开始");
            Intent intent = new Intent();
            intent.putExtra("ext",map.get("ext"));
            Bundle bundle = new Bundle();
            bundle.putString("ext",map.get("ext"));
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName("com.youdi.jzb","com.uzmap.pkg.LauncherUI");
            context.startActivity(intent);
            L.e("跳转结束");
        }
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        L.e("onNotificationMessageArrived");
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        L.e("onCommandResult");
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        L.e("onReceiveRegisterResult");
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        L.e(cmdArg1);
        // 初始化
        ThirdPushTokenMgr.getInstance().setPushTokenToTIM(PrivateConstants.XM_PUSH_BUZID, cmdArg1);
    }

}
