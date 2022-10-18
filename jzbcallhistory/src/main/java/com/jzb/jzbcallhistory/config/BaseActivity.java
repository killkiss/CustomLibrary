package com.jzb.jzbcallhistory.config;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ViewConfiguration;

import java.lang.reflect.Method;

/**
 * create：2022/8/4 16:44
 *
 * @author ykx
 * @version 1.0
 * @Description 插件activity基础方法（待添加）
 */
public class BaseActivity implements ActivityInterface {

    @Override
    public void onCreate(Activity context, Bundle bundle) {
        L.e("-main-", "onCreate");
    }

    @Override
    public void onResume() {
        L.e("-main-", "onResume");
    }

    @Override
    public void onPause() {
        L.e("-main-", "onPause");
    }

    @Override
    public void onRestart() {
        L.e("-main-", "onRestart");
    }

    @Override
    public void onStop() {
        L.e("-main-", "onStop");
    }

    @Override
    public void onDestroy() {
        L.e("-main-", "onDestroy");
    }

    @Override
    public void onBackPressed() {
        L.e("-main-", "onBackPressed");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        L.e("-main-", "onConfigurationChanged");
    }

    @Override
    public void hostCallBack(Activity context, String requestAPI, String data) {
        L.e("-main-", data);
    }
}
