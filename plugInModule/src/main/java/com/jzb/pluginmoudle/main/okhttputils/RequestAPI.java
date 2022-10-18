package com.jzb.pluginmoudle.main.okhttputils;

/**
 * 请求API
 */
public class RequestAPI {

    /**
     * param参数格式_开始
     */
    protected static final String ARGS_PARAM_BEGIN = "sessionId/=/";

    /**
     * param参数格式_serviceId
     */
    protected static final String ARGS_PARAM_SERVICEID = "/;/serviceId/=/";

    /**
     * param参数格式_结束
     */
    protected static final String ARGS_PARAM_DATAFORMAT = "/;/dataFormat/=/";

    /**
     * param参数格式_dataformat=2 默认JSON格式
     */
    protected static final int ARGS_PARAM_JSON = 2;

    /**
     * param参数格式_结束
     */
    public static final String ARGS_PARAM = "param";

    /**
     * data参数格式_结束
     */
    public static final String ARGS_DATA = "data";
    // 基础URL
    public static final String BASE_URL = "http://114.251.122.181:8080/jzb-1.0/";

    public static final String URL = "pages/springMvcEntry/service.do";
    // 应用版本更新URL
    public static final String UP_APP = "http://www.baidu.com";
    public static final String SERVICE = "service";
    public static final String OPER_TYPE = "operType";
    public static String SESSION_ID = "123";

    /**
     * 拼接完整URL
     *
     * @param relativeUrl
     * @return String
     */
    public static String getAbsoluteUrl(String relativeUrl) {
        return RequestAPI.BASE_URL + relativeUrl;
    }

    /**
     * 拼接URL
     *
     * @return String
     */
    public static String getAbsoluteUrl() {
        return RequestAPI.BASE_URL + URL;
    }

    /**
     * 获得param部分参数
     */
    public static String getParam(String service) {
        return ARGS_PARAM_BEGIN + SESSION_ID + ARGS_PARAM_SERVICEID + service
                + ARGS_PARAM_DATAFORMAT + ARGS_PARAM_JSON;
    }
}
