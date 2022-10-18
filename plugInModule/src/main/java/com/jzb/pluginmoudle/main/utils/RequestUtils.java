package com.jzb.pluginmoudle.main.utils;

import android.content.Context;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jzb.pluginmoudle.main.L;
import com.jzb.pluginmoudle.main.okhttputils.OkHttpUtils;
import com.jzb.pluginmoudle.main.okhttputils.callback.StringCallback;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;

/**
 * create：2022/8/17 9:39
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class RequestUtils {

    private static RequestUtils instance;
    public static RequestUtils getInstance() {
        if (instance == null) {
            synchronized (RequestUtils.class) {
                if (instance == null) {
                    instance = new RequestUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 请求搜索联系人列表
     *
     * @param context    上下文
     * @param requestAPI 接口url
     * @param params     携参
     * @param callback   回调
     */
    public void handlerNetRequestPostString(Context context, String requestAPI,
                                                JSONObject params, RequestCallback callback) {
        String token = params.getString("token");
        params.remove("token");
        OkHttpUtils.postString()
                .url(requestAPI)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .addHeader("token", token)
                .content(JSON.toJSONString(params))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Response response, Exception e, int id) {
                        Toast.makeText(context, "服务器异常", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        L.e(response);
                        if (callback != null){
                            callback.success(response);
                        }
                    }
                });
    }

    public interface RequestCallback {
        void success(String data);
    }
}
