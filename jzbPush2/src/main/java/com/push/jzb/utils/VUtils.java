package com.push.jzb.utils;

import static android.content.Context.VIBRATOR_SERVICE;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;

/**
 * create：2022/5/25 17:05
 *
 * @author ykx
 * @version 1.0
 * @Description 实现应用震动功能调用 [1000,1000]
 */
public class VUtils {

    private static VUtils instance;
    private Vibrator vibrator;

    public static VUtils getInstance() {
        if (instance == null) {
            synchronized (VUtils.class) {
                if (instance == null) {
                    instance = new VUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 创建震动
     */
    public void createVibrator(Context context) {
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
            return;
        }
        vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(new long[]{1000, 1000}, 0);
            timeLimit();
        }
    }

    /**
     * 创建震动
     */
    public void createVibrator2(Context context) {
        if (vibrator != null) {
            return;
        }
        vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(new long[]{1000, 1000}, 0);
            timeLimit();
        }
    }

    private TimeHandler timeHandler;

    private static class TimeHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            VUtils.getInstance().stopVibrator();
        }
    }

    private void timeLimit() {
        if (timeHandler != null) {
            timeHandler.removeCallbacksAndMessages(null);
            timeHandler = null;
        }
        timeHandler = new TimeHandler();
        timeHandler.sendEmptyMessageDelayed(0, 28 * 1000);
    }

    /**
     * 中断震动
     */
    public void stopVibrator() {
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
        if (timeHandler != null) {
            timeHandler.removeCallbacksAndMessages(null);
            timeHandler = null;
        }
    }
}
