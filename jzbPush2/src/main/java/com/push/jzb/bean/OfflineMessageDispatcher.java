package com.push.jzb.bean;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.push.jzb.utils.L;

public class OfflineMessageDispatcher {

    private static final String TAG = OfflineMessageDispatcher.class.getSimpleName();
    private static final String OEMMessageKey = "ext";
    private static final String TPNSMessageKey = "customContent";

    public static String parseOfflineMessage(Intent intent) {
        L.e("intent: " + intent);
        if (intent == null) {
            return null;
        }
        L.e("parse OEM push");
        Bundle bundle = intent.getExtras();
        L.e("bundle: " + bundle);
        String ext = bundle.getString(OEMMessageKey);
        L.e("push custom data ext: " + ext);
        if (TextUtils.isEmpty(ext)) {
            L.e("ext is null");
            return null;
        } else {
            return ext;
        }
//        if (bundle == null) {
//            // TPNS 的透传参数数据格式为 customContent:{data}
//            Uri uri = intent.getData();
//            if (uri == null) {
//                L.e( "intent.getData() uri is null");
//                return null;
//            } else {
//                return parseOfflineMessageTPNS(intent);
//            }
//        } else {
//            String ext = bundle.getString(OEMMessageKey);
//            L.e( "push custom data ext: " + ext);
//            if (TextUtils.isEmpty(ext)) {
//                L.e( "ext is null");
//                return null;
//            } else {
//                return ext;
//            }
//        }
    }

    private static OfflineMessageBean getOfflineMessageBeanFromContainer(String ext) {
        if (TextUtils.isEmpty(ext)) {
            return null;
        }
        OfflineMessageContainerBean bean = null;
        try {
            bean = new Gson().fromJson(ext, OfflineMessageContainerBean.class);
        } catch (Exception e) {
            L.e("getOfflineMessageBeanFromContainer: " + e.getMessage());
        }
        if (bean == null) {
            return null;
        }
        return offlineMessageBeanValidCheck(bean.entity);
    }

    private static OfflineMessageBean parseOfflineMessageTPNS(Intent intent) {
        L.e("parse TPNS push");
        //intent uri
        Uri uri = intent.getData();
        if (uri == null) {
            L.e("intent.getData() uri is null");
        } else {
            L.e("parseOfflineMessageTPNS get data uri: " + uri);
            String ext = uri.getQueryParameter(TPNSMessageKey);
            L.e("push custom data ext: " + ext);
            if (!TextUtils.isEmpty(ext)) {
                return getOfflineMessageBeanFromContainer(ext);
            } else {
                L.e("TextUtils.isEmpty(ext)");
            }
        }
        return null;
    }

    private static OfflineMessageBean getOfflineMessageBean(String ext) {
        if (TextUtils.isEmpty(ext)) {
            return null;
        }
        OfflineMessageBean bean = new Gson().fromJson(ext, OfflineMessageBean.class);
        return offlineMessageBeanValidCheck(bean);
    }

    private static OfflineMessageBean offlineMessageBeanValidCheck(OfflineMessageBean bean) {
        if (bean == null) {
            return null;
        } else if (bean.version != 1
                || (bean.action != OfflineMessageBean.REDIRECT_ACTION_CHAT
                && bean.action != OfflineMessageBean.REDIRECT_ACTION_CALL)) {
            return null;
        }
        return bean;
    }

}
