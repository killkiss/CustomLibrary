package com.jzb.jzbcallhistory.config;

/**
 * create：2022/7/5 15:04
 *
 * @author ykx
 * @version 1.0
 * @Description 插件模块-配置文件
 */
public class Constants {
    // 测试插件化模块当前版本信息
    public static String MODULE_TEST_VERSION = "0.0.2";
    public static String unionId;
    // 这部分信息应该写在ApiCloud中,通过热更新更新前端代码后检测是否有新版本模块需要下载
    // 测试插件化模块下载地址
    // String MODULE_TEST_DOWNLOAD_URL
    // 这部分信息应该写在ApiCloud中
    // 测试插件化模块使用资源前缀 范围 0x02 到 0x7e 唯一性
    public static String getModuleTestVersion() {
        return MODULE_TEST_VERSION;
    }
}
