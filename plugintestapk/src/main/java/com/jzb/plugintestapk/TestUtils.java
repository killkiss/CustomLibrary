package com.jzb.plugintestapk;

import android.content.Context;
import android.widget.Toast;

/**
 * create：2022/6/28 14:02
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class TestUtils {

    public static void showData(Context context) {
        Toast.makeText(context, "测试成功 + 版本" + Constants.MODULE_TEST_VERSION, Toast.LENGTH_SHORT).show();
    }
}
