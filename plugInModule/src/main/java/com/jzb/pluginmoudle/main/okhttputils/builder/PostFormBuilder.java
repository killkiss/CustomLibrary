package com.jzb.pluginmoudle.main.okhttputils.builder;

import com.alibaba.fastjson.JSONObject;
import com.jzb.pluginmoudle.main.okhttputils.OkHttpUtils;
import com.jzb.pluginmoudle.main.okhttputils.RequestAPI;
import com.jzb.pluginmoudle.main.okhttputils.request.PostFormRequest;
import com.jzb.pluginmoudle.main.okhttputils.request.RequestCall;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhy on 15/12/14.
 */
public class PostFormBuilder extends OkHttpRequestBuilder<PostFormBuilder> implements HasParamsable {
    private List<FileInput> files = new ArrayList<>();

    @Override
    public RequestCall build() {
        //添加全局参数
        Map<String, String> addParams = OkHttpUtils.getInstance().getGlobalParams().addParams();
        if (params != null) {
            addParams.putAll(params);
        }
        return new PostFormRequest(url, tag, addParams, headers, files, id).build();
    }

    public PostFormBuilder files(String key, Map<String, File> files) {
        for (String filename : files.keySet()) {
            this.files.add(new FileInput(key, filename, files.get(filename)));
        }
        return this;
    }

    public PostFormBuilder addFile(String name, String filename, File file) {
        files.add(new FileInput(name, filename, file));
        return this;
    }

    public static class FileInput {
        public String key;
        public String filename;
        public File file;

        public FileInput(String name, String filename, File file) {
            this.key = name;
            this.filename = filename;
            this.file = file;
        }

        @Override
        public String toString() {
            return "FileInput{" +
                    "key='" + key + '\'' +
                    ", filename='" + filename + '\'' +
                    ", file=" + file +
                    '}';
        }
    }


    @Override
    public OkHttpRequestBuilder cusParams(JSONObject params) {
        Map<String, String> m = new HashMap<String, String>();
        String service = params.getString(RequestAPI.SERVICE);
        m.put(RequestAPI.ARGS_DATA, params.toJSONString());
        m.put(RequestAPI.ARGS_PARAM, RequestAPI.getParam(service));
        if (this.params != null) {
            this.params.putAll(m);
        } else {
            this.params = m;
        }
        return this;
    }

    @Override
    public PostFormBuilder params(Map<String, String> params) {
        if (this.params != null) {
            this.params.putAll(params);
        } else {
            this.params = params;
        }
        return this;
    }

    @Override
    public PostFormBuilder addParam(String key, String val) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }


}
