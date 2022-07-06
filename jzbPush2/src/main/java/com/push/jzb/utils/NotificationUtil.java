package com.push.jzb.utils;

import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * create：2022/5/14 12:42
 *
 * @author ykx
 * @version 1.0
 * @Description 跳转到应用内通知栏权限处
 */
public class NotificationUtil {
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    //调用该方法获取是否开启通知栏权限
    public static boolean isNotifyEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return isEnableV26(context);
        } else {
            return isEnabledV19(context);
        }
    }

    /**
     * 8.0以下判断
     *
     * @param context api19  4.4及以上判断
     * @return
     */
    private static boolean isEnabledV19(Context context) {

        AppOpsManager mAppOps =
                (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null;

        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());

            Method checkOpNoThrowMethod =
                    appOpsClass.getMethod(CHECK_OP_NO_THROW,
                            Integer.TYPE, Integer.TYPE, String.class);

            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (Integer) opPostNotificationValue.get(Integer.class);

            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) ==
                    AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 8.0及以上通知权限判断
     *
     * @param context
     * @return
     */
    private static boolean isEnableV26(Context context) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        try {
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            Method sServiceField = notificationManager.getClass().getDeclaredMethod("getService");
            sServiceField.setAccessible(true);
            Object sService = sServiceField.invoke(notificationManager);

            Method method = sService.getClass().getDeclaredMethod("areNotificationsEnabledForPackage"
                    , String.class, Integer.TYPE);
            method.setAccessible(true);
            return (boolean) method.invoke(sService, pkg, uid);
        } catch (Exception e) {
            return true;
        }
    }

//    /**
//     * 是否启用通知监听服务
//     *
//     * @return
//     */
//    public boolean isNLServiceEnabled(Context context) {
//        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
//        if (packageNames.contains(context.getPackageName())) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 切换通知监听器服务
//     */
//    public void toggleNotificationListenerService(Context context, boolean isEnable) {
//        PackageManager pm = context.getPackageManager();
//        if (isEnable) {
//            pm.setComponentEnabledSetting(new ComponentName(context.getApplicationContext(), MyNotificationListenerService.class),
//                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//        } else {
//            pm.setComponentEnabledSetting(new ComponentName(context.getApplicationContext(), MyNotificationListenerService.class),
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
//        }
//    }
}
