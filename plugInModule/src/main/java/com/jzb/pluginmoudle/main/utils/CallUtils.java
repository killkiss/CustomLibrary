package com.jzb.pluginmoudle.main.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import com.jzb.pluginmoudle.main.L;
import com.jzb.pluginmoudle.main.widget.DialView;

/**
 * create：2022/8/10 11:38
 *
 * @author ykx
 * @version 1.0
 * @Description 拨打电话以及通话状态相关工具类
 */
public class CallUtils {


    private static volatile CallUtils instance;

    public static CallUtils getInstance() {
        if (instance == null) {
            synchronized (CallUtils.class) {
                if (instance == null) {
                    instance = new CallUtils();
                }
            }
        }
        return instance;
    }

    public void callPhone(Context context, String phoneNumber, DialView.CallPhoneBack callPhoneBack) {
        try {
            if (selfCheck(context)) {
                context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
                callPhoneBack.success(phoneNumber);
            }
        } catch (Exception e) {
            L.e(e.getMessage());
        }
    }

    /**
     * 自检
     *
     * @return 是否通过
     */
    public boolean selfCheck(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.CALL_PHONE}, 100);
            return false;
        }
        return true;
    }

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

}
