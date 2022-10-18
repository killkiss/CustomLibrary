package com.jzb.pluginmoudle.main.okhttputils.builder;

import java.util.Map;

import okhttp3.MediaType;
import com.jzb.pluginmoudle.main.okhttputils.OkHttpUtils;
import com.jzb.pluginmoudle.main.okhttputils.request.PostStringRequest;
import com.jzb.pluginmoudle.main.okhttputils.request.RequestCall;

/**
 * Created by zhy on 15/12/14.
 */
public class PostStringBuilder extends OkHttpRequestBuilder<PostStringBuilder> {
    private String content;
    private MediaType mediaType;


    public PostStringBuilder content(String content) {
        this.content = content;
        return this;
    }

    public PostStringBuilder mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public RequestCall build() {
        //添加全局参数
        Map<String, String> addParams = OkHttpUtils.getInstance().getGlobalParams().addParams();
        if (params != null) {
            addParams.putAll(params);
        }
        return new PostStringRequest(url, tag, addParams, headers, content, mediaType, id).build();
    }
}
