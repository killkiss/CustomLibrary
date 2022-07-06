package com.jzb.plugintestapk;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * create：2022/6/28 17:01
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class TestActivity implements ActivityInterface {

    @Override
    public void onCreate(Activity context, Bundle bundle) {
        Log.e("-main-", "onCreate");
        View view = context.getLayoutInflater().inflate(R.layout.plugin_activity_test, null);
        context.setContentView(view);
        TextView textView = view.findViewById(R.id.tv_content);
        if (bundle != null) {
            String version = bundle.getString("MODULE_VERSION");
            textView.setText("插件化APK版本号：" + version);
            showData(context, version);
        }
    }

    @Override
    public void onResume() {
        Log.e("-main-", "onResume");
    }

    @Override
    public void onPause() {
        Log.e("-main-", "onPause");
    }

    @Override
    public void onRestart() {
        Log.e("-main-", "onRestart");
    }

    @Override
    public void onStop() {
        Log.e("-main-", "onStop");
    }

    @Override
    public void onDestroy() {
        Log.e("-main-", "onDestroy");
    }

    public void showData(Context context, String version) {
        TestUtils.showData(context, version);
    }

}
