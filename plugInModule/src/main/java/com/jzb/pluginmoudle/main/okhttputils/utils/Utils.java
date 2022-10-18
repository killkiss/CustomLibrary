package com.jzb.pluginmoudle.main.okhttputils.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.jzb.pluginmoudle.main.okhttputils.OkHttpUtils;


/**
 * Created by Vector
 * on 2017/2/14 0014.
 */

public class Utils {
    public static Context getContext() {
        return OkHttpUtils.getInstance().getContext();
    }


    public static boolean isConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }


}
