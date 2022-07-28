package com.jzb.pluginmoudle.main.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import java.util.HashMap;

/**
 * create：2022/7/8 9:21
 *
 * @author ykx
 * @version 1.0
 * @Description 广播注册方法
 */
public class BroadcastUtils {

    // 存放已注册的广播
    private static HashMap<String, BroadcastReceiver> broadcastMap = new HashMap<>();

    /**
     * 注册广播
     * @param context 页面对象
     * @param classLoader apk加载器
     * @param action 别名
     * @param broadcastName 广播名
     */
    public static void registerBroadcastReceiver(Context context, ClassLoader classLoader,
                                                 String action, String broadcastName) {
        try {
            BroadcastReceiver receiver = (BroadcastReceiver) classLoader.loadClass(broadcastName).newInstance();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(action);
            context.registerReceiver(receiver,intentFilter);
            broadcastMap.put(action,receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注销广播
     * @param context 页面对象
     * @param action 别名
     */
    public static void unregisterBroadcastReceiver(Context context,String action){
        BroadcastReceiver receiver = broadcastMap.remove(action);
        context.unregisterReceiver(receiver);
    }
}
