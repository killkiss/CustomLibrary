package com.jzb.pluginmoudle.main.okhttputils.builder;

import java.util.Map;

import com.jzb.pluginmoudle.main.okhttputils.OkHttpUtils;
import com.jzb.pluginmoudle.main.okhttputils.request.OtherRequest;
import com.jzb.pluginmoudle.main.okhttputils.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder {
    @Override
    public RequestCall build() {
        //添加全局参数
        Map<String, String> addParams = OkHttpUtils.getInstance().getGlobalParams().addParams();
        if (params != null) {
            addParams.putAll(params);
        }
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, addParams, headers, id).build();
    }
}
