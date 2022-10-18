package com.jzb.jzbcallhistory.config;

import android.content.Context;
import android.content.Intent;

/**
 * create：2022/8/25 11:37
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class SendBroadcastUtil {

    public static void sendBroadcast(Context context, String ext, String requestAPI) {
        // 添加本地拨打记录
        Intent intent = new Intent("PlugInFakerActivity");
        intent.putExtra("ext", ext);
        intent.putExtra("requestAPI", requestAPI);
        context.sendBroadcast(intent);
    }
}