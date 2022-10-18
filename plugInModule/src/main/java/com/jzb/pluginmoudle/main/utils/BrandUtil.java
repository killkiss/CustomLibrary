package com.jzb.pluginmoudle.main.utils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.lang.reflect.Method;

public class BrandUtil {
    /**
     * 判断是否为小米设备
     */
    public static boolean isBrandXiaoMi() {
        return "xiaomi".equalsIgnoreCase(getBuildBrand())
                || "xiaomi".equalsIgnoreCase(getBuildManufacturer());
    }

    /**
     * 判断是否为华为设备
     */
    public static boolean isBrandHuawei() {
        return "huawei".equalsIgnoreCase(getBuildBrand()) ||
                "huawei".equalsIgnoreCase(getBuildManufacturer()) ||
                "honor".equalsIgnoreCase(getBuildBrand()) ||
                "honor".equalsIgnoreCase(getBuildManufacturer());
    }

    /**
     * 判断是否为魅族设备
     */
    public static boolean isBrandMeizu() {
        return "meizu".equalsIgnoreCase(getBuildBrand())
                || "meizu".equalsIgnoreCase(getBuildManufacturer())
                || "22c4185e".equalsIgnoreCase(getBuildBrand());
    }

    /**
     * 判断是否是 oppo 设备, 包含子品牌
     *
     * @return
     */
    public static boolean isBrandOppo() {
        return "oppo".equalsIgnoreCase(getBuildBrand()) ||
                "realme".equalsIgnoreCase(getBuildBrand()) ||
                "oneplus".equalsIgnoreCase(getBuildBrand()) ||
                "oppo".equalsIgnoreCase(getBuildManufacturer()) ||
                "realme".equalsIgnoreCase(getBuildManufacturer()) ||
                "oneplus".equalsIgnoreCase(getBuildManufacturer());
    }

    /**
     * 判断是否是vivo设备
     *
     * @return
     */
    public static boolean isBrandVivo() {
        return "vivo".equalsIgnoreCase(getBuildBrand())
                || "vivo".equalsIgnoreCase(getBuildManufacturer());
    }

    /**
     * 判断是否支持谷歌服务
     *
     * @return
     */
    public static boolean isGoogleServiceSupport() {
//        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
//        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(TUIOfflinePushService.appContext);
//        return resultCode == ConnectionResult.SUCCESS;
        return false;
    }

    /**
     * 获取ChannelId
     *
     * @return ChannelId
     */
    private static String channelId;

    public static String getChannelId() {
        if (TextUtils.isEmpty(channelId)) {
            if (isBrandHuawei()) {
                channelId = "yd_jzb";
            } else if (isBrandXiaoMi() || isBrandVivo()) {
                channelId = "high_system";
            } else if (isBrandOppo()) {
                channelId = "ytud_jizhibao";
            } else {
                channelId = "high_system";
            }
        }
        return channelId;
    }

    public static String getBuildBrand() {
        return TUIBuild.getBrand();
    }

    public static String getBuildManufacturer() {
        return TUIBuild.getManufacturer();
    }

    public static String getBuildModel() {
        return TUIBuild.getModel();
    }

    public static String getBuildVersionRelease() {
        return TUIBuild.getVersion();
    }

    public static int getBuildVersionSDKInt() {
        return TUIBuild.getVersionInt();
    }

    public static boolean hasPermissionBackRun(Context context) {
        boolean isAllow;
        if (Build.VERSION.SDK_INT >= 30 && !Settings.canDrawOverlays(context)) {
            isAllow = false;
        } else {
            isAllow = BrandUtil.isXiaomiBgStartPermissionAllowed(context);
        }
        if (!isAllow) {
            PopupWindowUtil.getInstance().showWindow((Activity) context,
                    "为了应用在后台时响应通话请求,请到设置中打开应用“后台弹出界面“权限", new PopupWindowUtil.OnClickCallBack() {
                        @Override
                        public void onClickSure() {
                            try {
                                Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                                intent.putExtra("extra_pkgname", context.getPackageName());
                                ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                                intent.setComponent(componentName);
                                context.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                goIntentSetting((Activity) context);
                            }
                        }

                        @Override
                        public void onClickCancel() {

                        }
                    });
        }
        return isAllow;
    }

    public static boolean isXiaomiBgStartPermissionAllowed(Context context) {
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int op = 10021;
        try {
            Method method = appOpsManager.getClass().getMethod("checkOpNoThrow", new Class[]{int.class, int.class, String.class});
            method.setAccessible(true);
            int result = (int) method.invoke(appOpsManager, op, android.os.Process.myUid(), context.getPackageName());
            return AppOpsManager.MODE_ALLOWED == result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 默认打开应用详细页
     */
    private static void goIntentSetting(Activity pActivity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", pActivity.getPackageName(), null);
        intent.setData(uri);
        try {
            pActivity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
