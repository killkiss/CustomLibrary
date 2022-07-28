package com.push.jzb;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;
import static com.push.jzb.PrivateConstants.HW_PUSH_BUZID;
import static com.push.jzb.service.MyService.SDK_ID;
import static com.push.jzb.service.MyService.USER_ID;
import static com.push.jzb.service.MyService.USER_SIG;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Process;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.heytap.msp.push.HeytapPushManager;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessaging;
import com.meizu.cloud.pushsdk.PushManager;
import com.meizu.cloud.pushsdk.util.MzSystemUtils;
import com.push.jzb.bean.OfflineMessageDispatcher;
import com.push.jzb.notification.NotificationClickReceiver;
import com.push.jzb.notification.NotifyBean;
import com.push.jzb.service.MyService;
import com.push.jzb.service.OPPOPushImpl;
import com.push.jzb.utils.BrandUtil;
import com.push.jzb.utils.L;
import com.push.jzb.utils.MobileInfoUtils;
import com.push.jzb.utils.NotificationUtil;
import com.push.jzb.utils.SecuritySharedPreference;
import com.push.jzb.utils.ThirdPushTokenMgr;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMConversationListener;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.vivo.push.PushClient;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * create：2022/5/9 10:42
 *
 * @author ykx
 * @version 1.0
 * @Description 推送相关 腾讯IM->连接华为,小米,ViVo,xiaomi,oppo厂商消息推送
 */
public class OfflinePushModule extends UZModule {

    private Intent intent;
    public static String temporaryExt = "";
    private String userID;
    private String userSig;
    private String sdkID;
    private static UZModuleContext notifyContext;
    // 应用当前运行环境
    private static boolean appRunStatus;

    public OfflinePushModule(UZWebView webView) {
        super(webView);
    }

    /**
     * 测试专用方法
     */
    public void jsmethod_setHuaWeiBadge(UZModuleContext moduleContext) {
        notifyContext = moduleContext;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createNotificationChannel(moduleContext.getContext(), "yd_jzb");
        }
        createNotify(moduleContext.getContext(), "title", "content", new Random().nextInt(1000000), "ext123", false);
//        initTPNS(moduleContext.getContext());
//        ((Activity) moduleContext.getContext()).finish();
    }

    /**
     * 初始化离线推送实现各厂推送功能并添加至腾讯IM
     */
    public void jsmethod_initOffLinePush(UZModuleContext moduleContext) {
        // 证书ID获取
        PrivateConstants.HW_PUSH_BUZID = moduleContext.optInt("HW_PUSH_BUZID");
        PrivateConstants.XM_PUSH_BUZID = moduleContext.optInt("XM_PUSH_BUZID");
        PrivateConstants.MZ_PUSH_BUZID = moduleContext.optInt("MZ_PUSH_BUZID");
        PrivateConstants.VIVO_PUSH_BUZID = moduleContext.optInt("VIVO_PUSH_BUZID");
        PrivateConstants.OPPO_PUSH_BUZID = moduleContext.optInt("OPPO_PUSH_BUZID");
        // 添加应用前台后台切换监听
        initActivityLifecycle(moduleContext.getContext().getApplicationContext());
        // 各厂通道初始化
        initPush(moduleContext.getContext());
    }

    /**
     * 实现后台震动控制逻辑
     *
     * @param moduleContext 前端传值
     */
    public void jsmethod_initOffLineVibrator(UZModuleContext moduleContext) {
        // 是否使用功能
        boolean order = moduleContext.optString("order").equals("use");
        jsmethod_setUserInfo(moduleContext);
        // 是否为可使用手机
        boolean isStart = BrandUtil.isBrandHuawei() || BrandUtil.isBrandXiaoMi() || BrandUtil.isBrandOppo();
        SecuritySharedPreference securitySharedPreference = new SecuritySharedPreference(moduleContext.getContext(), "openNotifyStatus", Context.MODE_PRIVATE);
        // 权限提醒只执行一次
        boolean firstIgnoringBatteryOptimizations = securitySharedPreference.getBoolean("firstDoBackgroundPermission", true);
        if (firstIgnoringBatteryOptimizations && order && isStart) {
            securitySharedPreference.edit().putBoolean("firstDoBackgroundPermission", false).apply();
            new AlertDialog.Builder(moduleContext.getContext())
                    .setTitle("权限提醒")
                    .setMessage("如需应用处于后台时能接收音视频通话提醒，请您开启本应用的" + "\"允许后台活动\"" + "权限")
                    .setPositiveButton("确认", (dialogInterface, i) -> {
                        if (BrandUtil.isBrandHuawei() || BrandUtil.isBrandOppo()) {
                            MobileInfoUtils.jumpStartInterface(moduleContext.getContext());
                        } else if (BrandUtil.isBrandXiaoMi()) {
                            isIgnoringBatteryOptimizations(moduleContext.getContext(), true);
                        }
                    })
                    .setNegativeButton("取消", (dialogInterface, i) -> {
                        JSONObject ret = new JSONObject();
                        try {
                            ret.put("btn", "yes");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        moduleContext.success(ret, true);
                    })
                    .create()
                    .show();
        } else {
            new AlertDialog.Builder(moduleContext.getContext())
                    .setTitle("退出提示")
                    .setMessage("确定要退出程序吗？")
                    .setPositiveButton("确认", (dialogInterface, i) -> {
                        if (order && BrandUtil.isBrandHuawei()) {
                            initTPNS(moduleContext.getContext());
                        } else if (order && BrandUtil.isBrandOppo()) {
                            initTPNS(moduleContext.getContext());
                        } else if (order && BrandUtil.isBrandXiaoMi()) {
                            // 开启电池优化
                            // 电池优化保证后台运行不被杀死
                            initTPNS(moduleContext.getContext());
                        }
                        JSONObject ret = new JSONObject();
                        try {
                            ret.put("btn", "yes");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        moduleContext.success(ret, true);
                    })
                    .setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create()
                    .show();
        }
    }

    /**
     * 删除现有渠道
     *
     * @param moduleContext 前端对象
     */
    public void jsmethod_deleteChannel(UZModuleContext moduleContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = moduleContext.optString("channelKey");
            NotificationManager manager = (NotificationManager) moduleContext.getContext().getSystemService(NOTIFICATION_SERVICE);
            List<NotificationChannel> channels = manager.getNotificationChannels();
            for (int i = 0; i < channels.size(); i++) {
                NotificationChannel notificationChannel = channels.get(i);
                if (notificationChannel.getId() != null) {
                    L.e(notificationChannel.getId() + " = channelID");
                    if (notificationChannel.getId().equals(id)) {
                        manager.deleteNotificationChannel(notificationChannel.getId());
                        break;
                    }
                }
            }
        }
    }

    /**
     * 创建震动
     *
     * @param moduleContext 前端对象
     */
    public void jsmethod_createVibrator(UZModuleContext moduleContext) {
        String pattern = moduleContext.optString("pattern");
        Vibrator vibrator = (Vibrator) moduleContext.getContext().getSystemService(VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            if (!TextUtils.isEmpty(pattern)) {
                String[] patterns = pattern.split(",");
                long[] longPatterns = new long[patterns.length];
                for (int i = 0; i < patterns.length; i++) {
                    longPatterns[i] = Long.parseLong(patterns[i]);
                }
                vibrator.vibrate(longPatterns, -1);
            } else {
                vibrator.vibrate(new long[]{100, 500, 100, 500}, -1);
            }
        }
    }

    /**
     * 关闭应用通知栏消息
     */
    public void jsmethod_notifyMessageCloseAll(UZModuleContext context) {
        // 获取通知管理器
        NotificationManager manager = (NotificationManager) context.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        int id = context.optInt("id");
        // 关闭应用全部通知提醒
        if (id == 0) {
            manager.cancelAll();
        } else {
            manager.cancel(id);
        }
    }

    /**
     * 创建通知
     */
    public void jsmethod_createNotify(UZModuleContext moduleContext) {
        notifyContext = moduleContext;
        String title = moduleContext.optString("title");
        String content = moduleContext.optString("content");
        int id = moduleContext.optInt("messageID");
        String ext = moduleContext.optString("ext");
        boolean isAt = moduleContext.optBoolean("isAt");
        createNotify(moduleContext.getContext(), title, content, id, ext, isAt);
    }

    /**
     * 创建通知
     *
     * @param title   标题
     * @param content 内容
     * @param ext     携带参数
     * @param isAt    是否是@或者催消息
     */
    public void createNotify(Context context, String title, String content, int messageID, String ext, boolean isAt) {
        L.e("创建通知");
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // 将来意图，⽤于点击通知之后的操作,内部的new intent()可⽤于跳转等操作
        Intent intent = new Intent(context, NotificationClickReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putString("ext", ext);
        intent.putExtras(bundle);
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(context,
                (int) (Math.random() * 1000000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 通知栏构造器,创建通知栏样式
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        // 渠道ID
        String channelId;
        channelId = isAt ? "high_special" : BrandUtil.getChannelId();
        L.e(channelId);
        // 设置通知栏标题
        mBuilder.setContentTitle(title)
                // 设置通知栏显⽰内容
                .setContentText(content)
                // 设置通知栏点击意图
                .setContentIntent(mPendingIntent)
                // 通知产⽣的时间，会在通知信息⾥显⽰，⼀般是系统获取到的时间
                .setWhen(System.currentTimeMillis())
                // 设置该通知优先级
                .setPriority(Notification.PRIORITY_HIGH)
                // 设置渠道
                .setChannelId(channelId)
                // 设置这个标志当⽤户单击⾯板就可以让通知将⾃动取消
                .setAutoCancel(true)
                // 使⽤当前的⽤户默认设置
                .setDefaults(Notification.DEFAULT_VIBRATE)
                // 设置通知⼩ICON(应⽤默认图标)
                .setSmallIcon(R.drawable.jzbpush_logo);
        if (!TextUtils.isEmpty(ext)) {
            mNotificationManager.notify(messageID, mBuilder.build());
            // 特殊消息未知原因无法控制震动 手动创建震动
            if (isAt) {
                Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(new long[]{0, 1000}, -1);
                }
            }
        }
    }

    /**
     * 处理通知点击后的回调
     *
     * @param bean 通知信息
     */
    public static void handlerMessageCallBack(NotifyBean bean) {
        if (bean != null && notifyContext != null) {
            // 当应用处于后台时
            if (!appRunStatus) {
                // 处理点击事件需要唤醒页面
                ActivityManager activityManager = (ActivityManager) notifyContext.getContext().getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(20);
                for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                    // 找到本应用的 task，并将它切换到前台
                    if (taskInfo.baseActivity.getPackageName().equals(notifyContext.getContext().getPackageName())) {
                        activityManager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                        break;
                    }
                }
            }
            L.e("处理回调," + bean.getExt());
            JSONObject ret = new JSONObject();
            try {
                ret.put("conversationId", bean.getExt());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            notifyContext.success(ret);
        }
    }

    /**
     * 获取从intent携带的离线消息数据 并反馈给前端页面
     */
    public void jsmethod_getPushData(UZModuleContext moduleContext) {
        Activity activity = (Activity) moduleContext.getContext();
        String ext;
        if (intent == null) {
            // 第一次启动满足离线推送启动要求
            intent = activity.getIntent();
            ext = OfflineMessageDispatcher.parseOfflineMessage(intent);
        } else {
            // 第n次启动intent没有更新不满足离线推送逻辑要求
            // 功能已转至前端实现
            ext = "";
            temporaryExt = "";
        }
        if (TextUtils.isEmpty(ext)) {
            return;
        }
        JSONObject ret = new JSONObject();
        try {
            ret.put("conversationId", ext);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        moduleContext.success(ret, true);
    }

    /**
     * 登录后设置app信息
     */
    public void jsmethod_setUserInfo(UZModuleContext moduleContext) {
        userID = moduleContext.optString(USER_ID);
        userSig = moduleContext.optString(USER_SIG);
        sdkID = moduleContext.optString(SDK_ID);
    }

    /**
     * 创建渠道通知
     *
     * @param context   上下文
     * @param channelID 渠道ID
     */
    private void createNotificationChannel(Context context, String channelID) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelID, "默认推送", NotificationManager.IMPORTANCE_HIGH);
            channel.setVibrationPattern(new long[]{100, 500, 100, 500});
            channel.enableVibration(true);
//            channel.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bell), Notification.AUDIO_ATTRIBUTES_DEFAULT);
            manager.createNotificationChannel(channel);

            // 创建特殊消息类型
            manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            channel = new NotificationChannel("high_special", "特殊消息", NotificationManager.IMPORTANCE_HIGH);
            channel.setVibrationPattern(new long[]{0});
            channel.enableVibration(false);
//            channel.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bell), Notification.AUDIO_ATTRIBUTES_DEFAULT);
            manager.createNotificationChannel(channel);
        }
    }

    /**
     * 小米开启后台权限
     */
    private boolean isAllowed(Context context) {
        AppOpsManager ops = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        try {
            int op = 10021;
            Method method = ops.getClass().getMethod("checkOpNoThrow", int.class, int.class, String.class);
            Integer result = (Integer) method.invoke(ops, op, Process.myUid(), context.getPackageName());
            if (result == null) {
                return false;
            }
            return result == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            L.e("not support");
        }
        return false;
    }

    /**
     * 打开应用通知权限设置
     */
    private void openNotifyPermission(Context context) {
        SecuritySharedPreference securitySharedPreference = new SecuritySharedPreference(context, "openNotifyStatus", Context.MODE_PRIVATE);
        if (!NotificationUtil.isNotifyEnabled(context)) {
            boolean firstGetNotifyStatus = securitySharedPreference.getBoolean("firstGetNotifyStatus", true);
            if (firstGetNotifyStatus) {
                securitySharedPreference.edit().putBoolean("firstGetNotifyStatus", false).apply();
                new AlertDialog.Builder(context)
                        .setTitle("离线推送通知栏权限")
                        .setMessage("是否开启通知栏权限，否则无法收到离线消息")
                        .setPositiveButton("确认", (dialogInterface, i) -> {
                            Intent localIntent = new Intent();
                            // 直接跳转到应用通知设置的代码：
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0及以上
                                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
                            } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                // 5.0以上到8.0以下
                                localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                localIntent.putExtra("app_package", context.getPackageName());
                                localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
                            } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                                // 4.4
                                localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                localIntent.setData(Uri.parse("package:" + context.getPackageName()));
                            }
                            startActivity(localIntent);
                        })
                        .setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss())
                        .create()
                        .show();
            }
        }
    }

    /**
     * 添加应用前台后台切换监听
     */
    private void initActivityLifecycle(Context appContext) {
        if (appContext instanceof Application) {
            ((Application) appContext).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                private int foregroundActivities = 1;
                private boolean isChangingConfiguration;

                private final V2TIMConversationListener unreadListener = new V2TIMConversationListener() {
                    @Override
                    public void onTotalUnreadMessageCountChanged(long totalUnreadCount) {
                        L.e("华为设置角标");
                    }

                    @Override
                    public void onConversationChanged(List<V2TIMConversation> conversationList) {
                        super.onConversationChanged(conversationList);
                        for (int i = 0; i < conversationList.size(); i++) {
                            String lastMessageExt = Arrays.toString(conversationList.get(i).getLastMessage().getOfflinePushInfo().getExt());
                            L.e("lastMessageExt = " + lastMessageExt);
                        }
                    }
                };

                @Override
                public void onActivityCreated(Activity activity, Bundle bundle) {
                    if (bundle != null) { // 若bundle不为空则程序异常结束
                        L.e("重启整个程序");
                    }
                }

                @Override
                public void onActivityStarted(Activity activity) {
                    foregroundActivities++;
                    if (foregroundActivities == 1 && !isChangingConfiguration) {
                        // 应用切到前台
                        L.e("application enter foreground");
                        appRunStatus = true;
                        V2TIMManager.getOfflinePushManager().doForeground(new V2TIMCallback() {
                            @Override
                            public void onError(int code, String desc) {
                                L.e("doBackground err = " + code + ", desc = " + desc);
                            }

                            @Override
                            public void onSuccess() {
                                L.e("success");
                            }
                        });
                        V2TIMManager.getConversationManager().removeConversationListener(unreadListener);
                    }
                    isChangingConfiguration = false;
                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {
                    foregroundActivities--;
                    if (foregroundActivities == 0) {
                        // 应用切到后台
                        L.e("application enter background");
                        appRunStatus = false;
                        V2TIMManager.getConversationManager().getTotalUnreadMessageCount(new V2TIMValueCallback<Long>() {
                            @Override
                            public void onSuccess(Long aLong) {
                                int totalCount = aLong.intValue();
//                                V2TIMManager.getOfflinePushManager().doBackground(totalCount, new V2TIMCallback() {
//                                    @Override
//                                    public void onError(int code, String desc) {
//                                        L.e("doBackground err = " +
//                                                code + ", desc = " + desc);
//                                    }
//
//                                    @Override
//                                    public void onSuccess() {
//                                        L.e("success");
//                                    }
//                                });
                            }

                            @Override
                            public void onError(int code, String desc) {
                                L.e("doBackground err = " + code + ", desc = " + desc);
                            }
                        });
                        V2TIMManager.getConversationManager().addConversationListener(unreadListener);
                    }
                    isChangingConfiguration = activity.isChangingConfigurations();
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    L.e("程序已关闭");
//                    initTPNS(activity);
                }
            });
            V2TIMManager.getOfflinePushManager().doForeground(new V2TIMCallback() {
                @Override
                public void onError(int code, String desc) {
                    L.e("doBackground err = " + code + ", desc = " + desc);
                }

                @Override
                public void onSuccess() {
                    L.e("success");
                }
            });
        }
    }

    /**
     * 各厂推送功能初始化
     */
    private void initPush(Context context) {
        if (context != null) {
            Intent intentOne = new Intent(context, MyService.class);
            context.stopService(intentOne);
            if (BrandUtil.isBrandXiaoMi()) {
                openNotifyPermission(context);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    createNotificationChannel(context, "high_system");
                }
                L.e("supportBackPermission = " + isAllowed(context));
                // 小米离线推送
                MiPushClient.registerPush(context, PrivateConstants.XM_PUSH_APPID, PrivateConstants.XM_PUSH_APPKEY);
            } else if (BrandUtil.isBrandHuawei()) {
                openNotifyPermission(context);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    createNotificationChannel(context, "yd_jzb");
                }
                // 华为离线推送，设置是否接收Push通知栏消息调用示例
                HmsMessaging.getInstance(context).turnOnPush().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        L.e("huawei turnOnPush Complete");
                    } else {
                        L.e("huawei turnOnPush failed: ret=" + task.getException().getMessage());
                    }
                });
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            // read from agconnect-services.json
                            String appId = AGConnectServicesConfig.fromContext(context).getString("client/app_id");
                            String token = HmsInstanceId.getInstance(context).getToken(appId, "HCM");
                            L.e("huawei get token:" + token);
                            if (!TextUtils.isEmpty(token)) {
                                // 该 token 需要保存并调用 setOfflinePushConfig 接口上报 IMSDK
                                ThirdPushTokenMgr.getInstance().setPushTokenToTIM(HW_PUSH_BUZID, token);
                            }
                        } catch (ApiException e) {
                            L.e("huawei get token failed, " + e);
                        }
                    }
                }.start();
            } else if (MzSystemUtils.isBrandMeizu(context)) {
                openNotifyPermission(context);
                // 魅族离线推送
                PushManager.register(context, PrivateConstants.MZ_PUSH_APPID, PrivateConstants.MZ_PUSH_APPKEY);
            } else if (BrandUtil.isBrandOppo()) {
                try {
                    boolean support = HeytapPushManager.isSupportPush(context);
                    if (support) {
                        // OPPO 手机默认关闭通知，需要申请
                        HeytapPushManager.requestNotificationPermission();
                        // oppo离线推送
                        OPPOPushImpl oppo = new OPPOPushImpl();
                        oppo.createNotificationChannel(context);
                        // oppo接入文档要求，应用必须要调用init(...)接口，才能执行后续操作。
//                        HeytapPushManager.init(context, false);
                        HeytapPushManager.register(context, PrivateConstants.OPPO_PUSH_APPKEY, PrivateConstants.OPPO_PUSH_APPSECRET, oppo);
                        L.e("register oppo");
                    }
                } catch (NoClassDefFoundError | NullPointerException | IllegalArgumentException e) {
                    L.e("oppo离线推送异常");
                }
            } else if (BrandUtil.isBrandVivo()) {
                openNotifyPermission(context);
                PushClient.getInstance(context).initialize();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    createNotificationChannel(context, "high_system");
                }
                L.e("vivo support push: " + PushClient.getInstance(context).isSupport());
                PushClient.getInstance(context).turnOnPush(state -> {
                    if (state == 0) {
                        String regId = PushClient.getInstance(context).getRegId();
                        L.e(regId + " = vivo");
                        ThirdPushTokenMgr.getInstance().setPushTokenToTIM(PrivateConstants.VIVO_PUSH_BUZID, regId);
                    } else {
                        // 根据vivo推送文档说明，state = 101 表示该vivo机型或者版本不支持vivo推送，链接：https://dev.vivo.com.cn/documentCenter/doc/156
                        L.e("vivopush open vivo push fail state = " + state);
                    }
                });
            } else {
                openNotifyPermission(context);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    createNotificationChannel(context, "high_system");
                }
                L.e("supportBackPermission = " + isAllowed(context));
                // 小米离线推送
                MiPushClient.registerPush(context, PrivateConstants.XM_PUSH_APPID, PrivateConstants.XM_PUSH_APPKEY);
            }
        }
    }

    /**
     * 原TPNS服务，现后台震动控制
     *
     * @param context 上下文
     */
    private void initTPNS(Context context) {
        Intent intentOne = new Intent(context, MyService.class);
        intentOne.putExtra("userid", userID);
        intentOne.putExtra("userSig", userSig);
        intentOne.putExtra("sdkAppid", sdkID);
        context.stopService(intentOne);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentOne);
        } else {
            context.startService(intentOne);
        }
//        L.e(V2TIMManager.getInstance().getVersion());
//        L.e(V2TIMManager.getInstance().getLoginUser());
//        XGPushManager.registerPush(context, new XGIOperateCallback() {
//            @Override
//            public void onSuccess(Object data, int flag) {
//                // token在设备卸载重装的时候有可能会变
//                L.e("注册成功，设备token为：" + data);
//            }
//
//            @Override
//            public void onFail(Object data, int errCode, String msg) {
//                L.e("注册失败，错误码：" + errCode + ",错误信息：" + msg);
//            }
//        });
    }

    /**
     * 开启电池优化权限
     */
    private boolean isIgnoringBatteryOptimizations(Context context, boolean requestPermission) {
        boolean isIgnoring = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (powerManager != null) {
                isIgnoring = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
            }
            if (!isIgnoring && requestPermission) {
                requestIgnoreBatteryOptimizations(context);
            }
        }
        return isIgnoring;
    }

    /**
     * 申请电池优化
     *
     * @param context 上下文
     */
    public void requestIgnoreBatteryOptimizations(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent;
                intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
