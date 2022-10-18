package com.jzb.pluginmoudle.main.okhttputils.builder;

import android.net.Uri;

import com.alibaba.fastjson.JSONObject;
import com.jzb.pluginmoudle.main.okhttputils.OkHttpUtils;
import com.jzb.pluginmoudle.main.okhttputils.RequestAPI;
import com.jzb.pluginmoudle.main.okhttputils.request.GetRequest;
import com.jzb.pluginmoudle.main.okhttputils.request.RequestCall;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhy on 15/12/14.
 */
public class GetBuilder extends OkHttpRequestBuilder<GetBuilder> implements HasParamsable {
    @Override
    public RequestCall build() {
        //添加全局参数
        Map<String, String> addParams = OkHttpUtils.getInstance().getGlobalParams().addParams();
        if (params != null) {
            addParams.putAll(params);
        }
        if (addParams != null) {
            url = appendParams(url, addParams);
        }
        return new GetRequest(url, tag, addParams, headers, id).build();
    }

    protected String appendParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            builder.appendQueryParameter(key, params.get(key));
        }
        return builder.build().toString();
    }


    @Override
    public GetBuilder params(Map<String, String> params) {
        if (this.params != null) {
            this.params.putAll(params);
        } else {
            this.params = params;
        }
        return this;
    }

    @Override
    public OkHttpRequestBuilder cusParams(JSONObject params) {
        Map<String, String> m = new HashMap<>();
//        String service = params.getString(RequestAPI.SERVICE);
//        m.put(RequestAPI.ARGS_PARAM, RequestAPI.getParam(service));
        m.put(RequestAPI.ARGS_DATA, params.toJSONString());
        if (this.params != null) {
            this.params.putAll(m);
        } else {
            this.params = m;
        }
        return this;
    }

    @Override
    public GetBuilder addParam(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

}
