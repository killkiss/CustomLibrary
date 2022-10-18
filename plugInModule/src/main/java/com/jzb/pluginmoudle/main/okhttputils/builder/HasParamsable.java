package com.jzb.pluginmoudle.main.okhttputils.builder;


import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Created by zhy on 16/3/1.
 */
public interface HasParamsable {
    OkHttpRequestBuilder params(Map<String, String> params);

    OkHttpRequestBuilder cusParams(JSONObject params);

    OkHttpRequestBuilder addParam(String key, String val);
}
