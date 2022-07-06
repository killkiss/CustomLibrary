package com.push.jzb.service;


import static com.push.jzb.utils.L.IS_DEBUG;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.push.jzb.PrivateConstants;
import com.push.jzb.R;
import com.push.jzb.bean.DataBean;
import com.push.jzb.bean.DataInBean;
import com.push.jzb.utils.BrandUtil;
import com.push.jzb.utils.L;
import com.push.jzb.utils.VUtils;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMConversationListener;
import com.tencent.imsdk.v2.V2TIMCustomElem;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMOfflinePushInfo;
import com.tencent.imsdk.v2.V2TIMSDKConfig;
import com.uzmap.pkg.LauncherUI;

import java.util.List;

/**
 * create：2022/5/28 10:57
 *
 * @author ykx
 * @version 1.0
 * @Description 自定义service保活逻辑 开启腾讯TPNS服务
 */
public class MyService extends Service {

    // 存储用户信息key值
    public static String USER_ID = "userid";
    public static String USER_SIG = "userSig";
    public static String SDK_ID = "sdkAppid";
    // 用户信息
    private String userID;
    private String sdkID;
    private String userSig;
    // 重新连接的标识
    private String lastMessageID = "";

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        customStartForeground();
        // 登录信息本地化存储
        if (IS_DEBUG) {
            // 测试用数据
            sdkID = "1400622631";
            userID = "0F4EF3F2-33CC-4B3F-80F8-C59900E88F16";
            userSig = "eJw1jtEKgjAYhd9lt6X8c3P9Cd203IUJwYyI7ixXDStElwnRuydal*c7fIfzJts0801X2dqQSABHgOnAWlOTiAQ*kDE3RZlXlS1IRDmACALB6NjYwjycPdtBAMVjxVTgMSalx5dMeQgKPRnO5wAxoqLiv2gvvYBO64NcJW7H9yfTGcxvzwQzejVaTdYbkcYGEt0c21e5*InO3vuzVIQc*YyG8PkCIro24Q__";
        } else {
            sdkID = intent.getStringExtra(SDK_ID);
            userID = intent.getStringExtra(USER_ID);
            userSig = intent.getStringExtra(USER_SIG);
        }
        if (!TextUtils.isEmpty(sdkID)) {
            V2TIMManager.getInstance().unInitSDK();
            // 1. 从 IM 控制台获取应用 SDKAppID，详情请参考 SDKAppID。
            // 2. 初始化 config 对象
            V2TIMSDKConfig config = new V2TIMSDKConfig();
            // 3. 指定 log 输出级别，详情请参考 SDKConfig。
            config.setLogLevel(V2TIMSDKConfig.V2TIM_LOG_NONE);
            // 4. 初始化 SDK 并设置 V2TIMSDKListener 的监听对象。
            // initSDK 后 SDK 会自动连接网络，网络连接状态可以在 V2TIMSDKListener 回调里面监听。
            L.e("初始化IM聊天");
            V2TIMManager.getInstance().initSDK(this, Integer.parseInt(sdkID), config);
            V2TIMManager.getInstance().login(userID, userSig, new V2TIMCallback() {
                @Override
                public void onSuccess() {
                    V2TIMManager.getConversationManager().setConversationListener(v2TIMConversationListener);
                }

                @Override
                public void onError(int i, String s) {
                    L.e("onError" + i + " / " + s);
                }
            });
        }
        return START_STICKY;
    }

    /**
     * 提升service优先级
     */
    private void customStartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = "appRunKey";
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // 判断是否有appRun的渠道 如果有则删除渠道 并重新创建
            List<NotificationChannel> channels = manager.getNotificationChannels();
            for (int i = 0; i < channels.size(); i++) {
                NotificationChannel notificationChannel = channels.get(i);
                if (notificationChannel.getId() != null) {
                    if (notificationChannel.getId().contains("appRunKey")) {
                        manager.deleteNotificationChannel(notificationChannel.getId());
                        id = "appRunKey" + System.currentTimeMillis();
                        break;
                    }
                }
            }
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // 创建渠道
            manager.createNotificationChannel(new NotificationChannel(id, "App Service", NotificationManager.IMPORTANCE_NONE));
            L.e("USER_CHANNEL_ID : " + id);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id);
            startForeground(10024, builder.build());
        }
    }

    private final V2TIMConversationListener v2TIMConversationListener = new V2TIMConversationListener() {

        @Override
        public void onTotalUnreadMessageCountChanged(long totalUnreadCount) {
            L.e("华为设置角标");
            updateBadge(MyService.this, (int) totalUnreadCount);
        }

        @Override
        public void onNewConversation(List<V2TIMConversation> conversationList) {
            L.e("触发onNewConversation");
            handlerV2TIMConversation(conversationList);
        }

        @Override
        public void onConversationChanged(List<V2TIMConversation> conversationList) {
            L.e("触发onConversationChanged");
            handlerV2TIMConversation(conversationList);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 停止可能存在的震动
        VUtils.getInstance().stopVibrator();
        // 退出前台运行模式
        stopForeground(true);
    }

    /**
     * 创建通知
     *
     * @param title   标题
     * @param content 内容
     * @param ext     携带参数
     * @param isAt    是否是@或者催消息
     */
    private void createNotify(String title, String content, String ext, boolean isAt) {
        L.e("创建通知");
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 将来意图，⽤于点击通知之后的操作,内部的new intent()可⽤于跳转等操作
        Intent intent = new Intent(this, LauncherUI.class);
        Bundle bundle = new Bundle();
        bundle.putString("ext", ext);
        intent.putExtras(bundle);
        PendingIntent mPendingIntent = PendingIntent.getActivity(this,
                (int) (Math.random() * 1000000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 通知栏构造器,创建通知栏样式
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
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
            mNotificationManager.notify((int) (Math.random() * 1000000), mBuilder.build());
            // 特殊消息未知原因无法控制震动 手动创建震动
            if (isAt) {
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(new long[]{0, 1000}, -1);
                }
            }
        }
    }

    /**
     * 华为更新角标
     *
     * @param context 上下文
     * @param number  数量
     */
    public void updateBadge(final Context context, final int number) {
        if (!BrandUtil.isBrandHuawei()) {
            return;
        }
        try {
            Bundle extra = new Bundle();
            extra.putString("package", context.getPackageName());
            extra.putString("class", PrivateConstants.BADGE_CLASS_NAME);
            extra.putInt("badgenumber", number);
            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, extra);
        } catch (Exception ignored) {
        }
    }

    /**
     * 处理会话列表消息
     */
    private void handlerV2TIMConversation(List<V2TIMConversation> conversationList) {
        for (int i = conversationList.size() - 1; i >= 0; i--) {
            // 会话列表消息更新
            V2TIMConversation conversation = conversationList.get(i);
            // 最新消息
            V2TIMMessage message = conversation.getLastMessage();
            // 消息体发送者
            String sender = message.getSender();
            // 排除主动发起的通话请求
            V2TIMOfflinePushInfo offlinePushInfo = message.getOfflinePushInfo();
            String title = offlinePushInfo.getTitle();
            String desc = offlinePushInfo.getDesc();
            L.e(title);
            L.e(desc);
            L.e(conversation.getConversationID());
            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc)) {
                if (lastMessageID.equals(message.getMsgID())) {
                    // 重复消息不再形成消息
                    return;
                }
                lastMessageID = message.getMsgID();
                // 被@的用户ID集合
                List<String> groupAtList = message.getGroupAtUserList();
                boolean isAt = false;
                if (groupAtList != null && groupAtList.size() > 0) {
                    for (String str : groupAtList) {
                        if (str.equals(userID)) {
                            isAt = true;
                            break;
                        }
                    }
                } else {
                    // 催消息是自定义消息
                    V2TIMCustomElem v2TIMCustomElem = message.getCustomElem();
                    if (v2TIMCustomElem != null) {
                        byte[] contentByte = v2TIMCustomElem.getData();
                        String content = new String(contentByte);
                        L.e(content + " = 自定义消息");
                        if (TextUtils.isEmpty(content) || sender.equals(userID)) break;
                        DataBean dataBean = new Gson().fromJson(content, DataBean.class);
                        // 催消息类型
                        if (dataBean.getType() == 6) {
                            // 区分私人聊天和群聊
                            int conversationType = conversation.getType();
                            // 私人聊天
                            if (conversationType == 1) {
                                isAt = true;
                            } else {
                                // 群聊
                                List<String> userIdList = dataBean.getUserIdList();
                                if (userIdList != null) {
                                    for (String str : userIdList) {
                                        if (str.equals(userID)) {
                                            L.e("催消息");
                                            isAt = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                createNotify(title, desc, conversation.getConversationID(), isAt);
            }
            V2TIMCustomElem v2TIMCustomElem = message.getCustomElem();
            L.e(v2TIMCustomElem + "");
            if (v2TIMCustomElem == null) {
                // 异常消息
                if (TextUtils.isEmpty(title) && TextUtils.isEmpty(desc)) {
                    VUtils.getInstance().stopVibrator();
                }
                return;
            }
            byte[] contentByte = v2TIMCustomElem.getData();
            String content = new String(contentByte);
            if (TextUtils.isEmpty(content) || sender.equals(userID)) return;
            DataBean dataBean = new Gson().fromJson(content, DataBean.class);
            if (dataBean == null) return;
            String data = dataBean.getData();
            if (TextUtils.isEmpty(data)) return;
            DataInBean bean = new Gson().fromJson(data, DataInBean.class);
            int callingType = bean.getCallingType();
            L.e(callingType + "");
            if (callingType == 200 || callingType == 201) {
                if (offlinePushInfo.getDesc().equals("您有一个通话请求")) {
                    // 重复请求判断会直接中断震动
                    gotoWakeLock(MyService.this);
                    VUtils.getInstance().createVibrator2(MyService.this);
                } else if (offlinePushInfo.getDesc().equals("对方已取消")) {
                    VUtils.getInstance().stopVibrator();
                }
            } else {
                VUtils.getInstance().stopVibrator();
            }
        }
    }

    /**
     * 屏幕唤醒
     */
    @SuppressLint("InvalidWakeLockTag")
    private void gotoWakeLock(Context context) {
        // 亮屏逻辑代码
        PowerManager mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "TAG");
        boolean screenOn = mPowerManager.isScreenOn();
        if (!screenOn) {
            //屏幕会持续点亮
            mWakeLock.acquire(10 * 1000L);
            //释放锁，以便10分钟后熄屏
            mWakeLock.release();
        }
    }
}