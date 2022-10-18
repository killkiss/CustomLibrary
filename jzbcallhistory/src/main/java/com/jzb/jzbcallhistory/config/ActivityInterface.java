package com.jzb.jzbcallhistory.config;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

/**
 * create：2022/7/1 10:44
 *
 * @author ykx
 * @version 1.0
 * @Description 插件模块-生命周期接口
 */
public interface ActivityInterface {
    // 生命周期方法
    void onCreate(Activity context, Bundle bundle);

    void onResume();

    void onPause();

    void onRestart();

    void onStop();

    void onDestroy();

    // 返回键监听
    void onBackPressed();

    // 翻转监听
    void onConfigurationChanged(Configuration newConfig);

    // 连接宿主的回调接口（插件发送广播调用宿主的方法后通过这个接口返回数据）
    void hostCallBack(Activity context,String requestAPI, String data);
}
