package com.jzb.pluginmoudle.main.okhttputils.utils;

import android.util.Log;

/**
 * Created by zhy on 15/11/6.
 */
public class OkHttpLog {
    private static boolean debug = false;

    public static void e(String msg) {
        if (debug) {
            Log.e("OkHttp", msg);
        }
    }

}

