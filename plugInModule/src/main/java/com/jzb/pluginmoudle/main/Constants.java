package com.jzb.pluginmoudle.main;

/**
 * create：2022/7/4 16:56
 *
 * @author ykx
 * @version 1.0
 * @Description 存储模块信息
 */
public class Constants {

    public static final String MODULE_VERSION = "MODULE_VERSION";
    public static final String MODULE_NAME = "MODULE_NAME";
    public static final String MODULE_DOWNLOAD_URL = "MODULE_DOWNLOAD_URL";
    public static final String MODULE_PACKAGE_ID = "MODULE_PACKAGE_ID";

    // 测试插件化模块名字
    public static final String MODULE_TEST = "testModule";
    public static final String MODULE_TEST_CONSTANTS = "com.jzb.plugintestapk.Constants";
    public static final String MODULE_TEST_APK = "plugInTest.project";
    // 测试插件化模块使用资源前缀
    public static final String MODULE_TEST_PACKAGE_ID = "0x70";

    // 视频插件化模块名字
    public static final String MODULE_MEDIA_TEST = "mediaModule";
    public static final String MODULE_MEDIA_CONSTANTS = "com.jzb.media.play.config.Constants";
    public static final String MODULE_MEDIA_APK = "jzbMediaPlay.project";
    // activity
    public static final String MODULE_MEDIA_ACTIVITY = "VideoPlayActivity";
    // 视频插件化模块使用资源前缀
    public static final String MODULE_MEDIA_PACKAGE_ID = "0x71";

    // 通话记录插件化模块名字
    public static final String MODULE_CALL_HISTORY = "callHistoryModule";
    public static final String MODULE_CALL_CONSTANTS = "com.jzb.jzbcallhistory.config.Constants";
    public static final String MODULE_CALL_APK = "jzbCallHistory.apk";
    // activity
    public static final String MODULE_CALL_ACTIVITY = "CallHistoryActivity";
    // 通话记录插件化模块使用资源前缀
    public static final String MODULE_CALL_PACKAGE_ID = "0x72";
}