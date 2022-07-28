//package com.push.jzb.service;
//
//import android.content.Context;
//import android.media.AudioManager;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.support.annotation.NonNull;
//import android.text.TextUtils;
//
//import com.push.jzb.utils.L;
//import com.push.jzb.utils.VUtils;
//import com.tencent.android.tpush.XGPushBaseReceiver;
//import com.tencent.android.tpush.XGPushClickedResult;
//import com.tencent.android.tpush.XGPushRegisterResult;
//import com.tencent.android.tpush.XGPushShowedResult;
//import com.tencent.android.tpush.XGPushTextMessage;
//
//
///**
// * create：2022/5/26 16:30
// *
// * @author ykx
// * @version 1.0
// * @Description TPNS服务回调
// */
//public class MessageReceiver extends XGPushBaseReceiver {
//    @Override
//    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
//        String registerID = xgPushRegisterResult.getToken();
//        if (!TextUtils.isEmpty(registerID)) {
////            ThirdPushTokenMgr.getInstance().setPushTokenToTIM(0, registerID);
//        }
//    }
//
//    @Override
//    public void onUnregisterResult(Context context, int i) {
//        L.e("onUnregisterResult");
//    }
//
//    @Override
//    public void onSetTagResult(Context context, int i, String s) {
//        L.e("onSetTagResult");
//    }
//
//    @Override
//    public void onDeleteTagResult(Context context, int i, String s) {
//        L.e("onDeleteTagResult");
//    }
//
//    @Override
//    public void onSetAccountResult(Context context, int i, String s) {
//        L.e("onSetAccountResult");
//    }
//
//    @Override
//    public void onDeleteAccountResult(Context context, int i, String s) {
//        L.e("onDeleteAccountResult");
//    }
//
//    @Override
//    public void onSetAttributeResult(Context context, int i, String s) {
//        L.e("onSetAttributeResult");
//    }
//
//    @Override
//    public void onQueryTagsResult(Context context, int i, String s, String s1) {
//        L.e("onQueryTagsResult");
//    }
//
//    @Override
//    public void onDeleteAttributeResult(Context context, int i, String s) {
//        L.e("onDeleteAttributeResult");
//    }
//
//    @Override
//    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
//        L.e("onTextMessage");
//    }
//
//    @Override
//    public void onNotificationClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
//        L.e("onNotificationClickedResult");
//        VUtils.getInstance().stopVibrator();
//        if (timeHandler != null) {
//            timeHandler.removeCallbacksAndMessages(null);
//            timeHandler = null;
//        }
//    }
//
//    @Override
//    public void onNotificationShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
//        L.e("onNotificationShowedResult");
//        // 自定义离线消息
//        String offLineContent = xgPushShowedResult.getCustomContent();
//        // 判读是否是音视频类型
////        if (xgPushShowedResult.getContent().equals("收到一个通话请求")) {
////            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
////            if (audio.getRingerMode() != AudioManager.RINGER_MODE_SILENT){
////                // 添加震动
////                VUtils.getInstance().createVibrator(context);
////                // 控制震动最大时间
////                timeLimit();
////            }
////        } else if (xgPushShowedResult.getContent().equals("通话已取消")) {
////            VUtils.getInstance().stopVibrator();
////            if (timeHandler != null) {
////                timeHandler.removeCallbacksAndMessages(null);
////                timeHandler = null;
////            }
////        }
//    }
//
//    private TimeHandler timeHandler;
//
//    private static class TimeHandler extends Handler {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//            VUtils.getInstance().stopVibrator();
//        }
//    }
//
//    private void timeLimit() {
//        if (timeHandler != null) {
//            timeHandler.removeCallbacksAndMessages(null);
//            timeHandler = null;
//        }
//        timeHandler = new TimeHandler();
//        timeHandler.sendEmptyMessageDelayed(0, 15 * 1000);
//    }
//}
