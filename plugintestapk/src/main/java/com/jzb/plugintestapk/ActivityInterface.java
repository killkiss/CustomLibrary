package com.jzb.plugintestapk;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;

/**
 * create：2022/7/1 10:44
 *
 * @author ykx
 * @version 1.0
 * @Description 生命周期接口
 */
public interface ActivityInterface {
    void onCreate(Activity context, Bundle bundle);
    void onResume();
    void onPause();
    void onRestart();
    void onStop();
    void onDestroy();
}
