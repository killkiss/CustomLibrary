package com.jzb.pluginmoudle.main.okhttputils.builder;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import com.jzb.pluginmoudle.main.okhttputils.OkHttpUtils;
import com.jzb.pluginmoudle.main.okhttputils.request.PostFileRequest;
import com.jzb.pluginmoudle.main.okhttputils.request.RequestCall;

/**
 * Created by zhy on 15/12/14.
 */
public class PostFileBuilder extends OkHttpRequestBuilder<PostFileBuilder> {
    private File file;
    private MediaType mediaType;


    public OkHttpRequestBuilder file(File file) {
        this.file = file;
        return this;
    }

    public OkHttpRequestBuilder mediaType(MediaType mediaType) {
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
        return new PostFileRequest(url, tag, addParams, headers, file, mediaType, id).build();
    }


}
