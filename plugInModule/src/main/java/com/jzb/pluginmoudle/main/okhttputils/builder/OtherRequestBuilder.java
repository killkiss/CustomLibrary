package com.jzb.pluginmoudle.main.okhttputils.builder;

import java.util.Map;

import okhttp3.RequestBody;
import com.jzb.pluginmoudle.main.okhttputils.OkHttpUtils;
import com.jzb.pluginmoudle.main.okhttputils.request.OtherRequest;
import com.jzb.pluginmoudle.main.okhttputils.request.RequestCall;

/**
 * DELETE、PUT、PATCH等其他方法
 */
public class OtherRequestBuilder extends OkHttpRequestBuilder<OtherRequestBuilder> {
    private RequestBody requestBody;
    private String method;
    private String content;

    public OtherRequestBuilder(String method) {
        this.method = method;
    }

    @Override
    public RequestCall build() {
        //添加全局参数
        Map<String, String> addParams = OkHttpUtils.getInstance().getGlobalParams().addParams();
        if (params != null) {
            addParams.putAll(params);
        }
        return new OtherRequest(requestBody, content, method, url, tag, addParams, headers, id).build();
    }

    public OtherRequestBuilder requestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public OtherRequestBuilder requestBody(String content) {
        this.content = content;
        return this;
    }


}
