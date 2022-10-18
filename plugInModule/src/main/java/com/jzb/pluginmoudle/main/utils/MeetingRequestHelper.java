package com.jzb.pluginmoudle.main.utils;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;

/**
 * create：2022/8/24 9:56
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class MeetingRequestHelper {

    public static boolean handlerRequest(Context context, String requestAPI, String url,
                                         JSONObject intentObject, CallBack callBack) {
        JSONObject object = new JSONObject();
        object.put("token", intentObject.getString("token"));
        object.put("comId", intentObject.getString("comId"));
        // 创建通话记录
        switch (requestAPI) {
            case "IM_TEL_RECORD_CREATE":
                JSONObject ext = new JSONObject();
                // 对方unionId(通话-接收人unionId)
                ext.put("toId", intentObject.getString("toId"));
                // 通话类型(VOICE:语音,VIDEO:视频,ORDINARY_CALL:普通通话)
                ext.put("telType", intentObject.getString("telType"));
                // 通话时长(单位:s).约定:如果不传,初始为 0
                ext.put("duration", intentObject.getInteger("duration"));
                // 创建人unionId(通话-发起人unionId)
                ext.put("creatorId", intentObject.getString("unionId"));
                ext.put("phone", intentObject.getString("phoneNumber"));
                // 对方unionId(通话-接收人unionId)
                object.put("telRecord", ext);
                break;
            case "IM_TEL_RECORD_DELETE":
                object.put("toId", intentObject.getString("toId"));
                break;
            case "IM_TEL_RECORD_MULTI_DELETE":
                object.put("telIds", intentObject.getString("telIds"));
                break;
            case "IM_TEL_RECORD_GET":
                object.put("telId", intentObject.getString("telId"));
                break;
            case "IM_TEL_RECORD_MULTI_GET":
                // 查询字段列表(传入则查询指定的字段,不传入则查询全部)
                object.put("fetchFields", intentObject.getString("fetchFields"));
                // 通话主键Id
                object.put("telIds", intentObject.getString("telIds"));
                // 是否严格模式(默认否)(严格模式要求所有ID对应的对象都存在)
                object.put("strict", intentObject.getString("strict"));
                break;
            case "IM_TEL_RECORD_LIST":
                // 查询字段列表(传入则查询指定的字段,不传入则查询全部)
                object.put("fetchFields", intentObject.getString("fetchFields"));
                object.put("page", intentObject.getInteger("page"));
                object.put("size", 100);
                object.put("count", true);
                object.put("unionId", intentObject.getString("unionId"));
                break;
            case "IM_TEL_RECORD_DETAIL":
//                object.put("fetchFields", intentObject.getString("fetchFields"));
                object.put("page", intentObject.getInteger("page"));
                object.put("size", 100);
                object.put("count", true);
                object.put("unionId", intentObject.getString("unionId"));
                object.put("toId", intentObject.getString("toId"));
                break;
            case "USER_ACCOUNT_GET_ACCOUNT_INFO":
                object.put("unionid",intentObject.getString("unionId"));
                break;
            default:
                return false;
        }
        RequestUtils.getInstance().handlerNetRequestPostString(context, url, object, callBack::success);
        return true;
    }

    public interface CallBack {
        void success(String data);
    }
}
