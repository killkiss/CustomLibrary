package com.push.jzb;

public class PrivateConstants {

    /****** 华为离线推送参数start ******/
    // 在腾讯云控制台上传第三方推送证书后分配的证书ID
    public static long HW_PUSH_BUZID = 23483;
    // 海外证书 ID，不需要直接删掉
    public static final long HW_PUSH_BUZID_ABROAD = 0;
    // 角标参数，默认为应用的 launcher 界面的类名
    public static String BADGE_CLASS_NAME = "com.uzmap.pkg.LauncherUI";
    /****** 华为离线推送参数end ******/

    /****** 小米离线推送参数start ******/
    // 在腾讯云控制台上传第三方推送证书后分配的证书ID
    public static long XM_PUSH_BUZID = 23484;
    // 海外证书 ID，不需要直接删掉
    public static final long XM_PUSH_BUZID_ABROAD = 0;
    // 小米开放平台分配的应用APPID及APPKEY
    public static final String XM_PUSH_APPID = "2882303761517632800";
    public static final String XM_PUSH_APPKEY = "5251763253800";
    /****** 小米离线推送参数end ******/

    /****** 魅族离线推送参数start ******/
    // 在腾讯云控制台上传第三方推送证书后分配的证书ID
    public static long MZ_PUSH_BUZID = 23513;
    // 海外证书 ID，不需要直接删掉
    public static final long MZ_PUSH_BUZID_ABROAD = 0;
    // 魅族开放平台分配的应用APPID及APPKEY
    public static final String MZ_PUSH_APPID = "146262";
    public static final String MZ_PUSH_APPKEY = "0dd8a487c7ba42cbbd6882e75fc51193";
    /****** 魅族离线推送参数end ******/

    /****** vivo离线推送参数start ******/
    // 在腾讯云控制台上传第三方推送证书后分配的证书ID
    public static long VIVO_PUSH_BUZID = 23641;
    // 海外证书 ID，不需要直接删掉
    public static final long VIVO_PUSH_BUZID_ABROAD = 0;
    /****** vivo离线推送参数end ******/

    /****** google离线推送参数start ******/
    // 在腾讯云控制台上传第三方推送证书后分配的证书ID
    public static long GOOGLE_FCM_PUSH_BUZID = 0;
    // 海外证书 ID，不需要直接删掉
    public static final long GOOGLE_FCM_PUSH_BUZID_ABROAD = 0;
    /****** google离线推送参数end ******/

    /****** oppo离线推送参数start ******/
    // 在腾讯云控制台上传第三方推送证书后分配的证书ID
    public static long OPPO_PUSH_BUZID = 23514;
    // 海外证书 ID，不需要直接删掉
    public static final long OPPO_PUSH_BUZID_ABROAD = 0;
    // oppo开放平台分配的应用APPID及APPKEY
    public static final String OPPO_PUSH_APPKEY = "ehHsTDfLs7c4c488S8c0wOow";
    public static final String OPPO_PUSH_APPSECRET = "7F7Fd5569Ea9fc5bDb4785Ae350bcC92";
    /****** oppo离线推送参数end ******/

    /**
     *  是否选择 TPNS 方案接入，默认为 IM 厂商通道，不需要修改配置
     *
     *  @note 组件实现了厂商和 TPNS 两种方式，以此变量作为接入方案区分
     *
     *  - 当接入推送方案选择 TPNS 通道，设置 isTPNSChannel 为 true，推送由 TPNS 提供服务；
     *  - 当接入推送方案选择 IM 厂商通道，设置 isTPNSChannel 为 false，走厂商推送逻辑；
     */
    public static final boolean isTPNSChannel = false;
}
