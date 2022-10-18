package com.jzb.pluginmoudle.main;

import android.os.Build;
import android.os.Environment;
import android.util.Log;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * create：2022/8/2 14:20
 *
 * @author ykx
 * @version 1.0
 * @Description 异常处理接收器
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";
    private static final String PATH = Environment.getExternalStorageDirectory() + "/crash/log/";

    private volatile static CrashHandler mCrashHandler;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (mCrashHandler == null) {
            synchronized (CrashHandler.class) {
                if (mCrashHandler == null) {
                    mCrashHandler = new CrashHandler();
                }
            }
        }
        return mCrashHandler;
    }

    public void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        dumpExceptionToFile(e);
        uploadExceptionToServer();

        if (mDefaultHandler != null) {
            //系统默认的异常处理器来处理,否则由自己来处理
            mDefaultHandler.uncaughtException(t, e);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    //可以根据自己需求来,比如获取手机厂商、型号、系统版本、内存大小等等
    private void dumpExceptionToFile(Throwable e) {
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long timeMillis = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(timeMillis));
        File file = new File(PATH + time + ".trace");
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            pw.print("Android版本号 :");
            pw.println(Build.VERSION.RELEASE);
            pw.print("手机型号 :");
            pw.println(Build.MODEL);
            pw.print("CUP架构 :");
            pw.println(Build.CPU_ABI);
            e.printStackTrace(pw);
            pw.close();
        } catch (IOException ex) {
            Log.e(TAG, "dump crash info error");
        }

    }

    //	上传到Server
    private void uploadExceptionToServer() {
    }

}
