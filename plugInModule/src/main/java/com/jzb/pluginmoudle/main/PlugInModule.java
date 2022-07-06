package com.jzb.pluginmoudle.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jzb.pluginmoudle.main.bean.ModuleBean;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * create：2022/6/28 11:05
 *
 * @author ykx
 * @version 1.0
 * @Description 插件化开发主体
 */
public class PlugInModule extends UZModule {

    public PlugInModule(UZWebView webView) {
        super(webView);
    }

    /**
     * 加载插件模块的普通方法
     * 加载插件模块的Activity
     */
    public void jsmethod_loadTestModule(UZModuleContext context) {
        // 是否使用测试模块插件
        boolean useTestModule = context.optBoolean("useTestModule");
        String apiType = context.optString("apiType");
        String apiName = context.optString("apiName");
        if (useTestModule) {
            // 文件名
            String fileName = "plugInTest.apk";
            // 获取测试模块插件位置
            File testPlugInPath = DexClassLoader.getAssetsCacheFile(context.getContext(), fileName);
            ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
            String dexPath = new File(testPlugInPath.getAbsolutePath(), fileName).getAbsolutePath();
            File optimizedDirectory = new File(testPlugInPath, "pluginlib").getAbsoluteFile();
            File librarySearchPath = new File(testPlugInPath, "dexout");
            // 加载apk/jar文件
            DexClassLoader pluginClassLoader = new DexClassLoader(dexPath, optimizedDirectory, librarySearchPath.getAbsolutePath(), localClassLoader);
            // 普通方法
            if (apiType.equals("method")) {
                try {
                    // 获取需要调用的类名
                    Class<?> localClass;
                    localClass = pluginClassLoader.loadClass("com.jzb.plugintestapk.TestUtils");
                    Constructor<?> localConstructor = localClass.getConstructor();
                    Object instance = localConstructor.newInstance();
                    // 获取方法
                    Method method = localClass.getMethod(apiName, Context.class);
                    method.setAccessible(true);
                    // 调用方法
                    method.invoke(instance, context.getContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (apiType.equals("activity")) {
                // 启动Activity
                Intent intent = new Intent(context.getContext(), PlugInFakerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("apiName", apiName);
                intent.putExtras(bundle);
                context.getContext().startActivity(intent);
            }
        }
    }

    /**
     * 获取模块版本号
     *
     * @param context MODULE_NAME:模块名
     *                MODULE_VERSION:模块版本
     *                upgradeMessage:版本更新文本内容
     *                silentUpdate:判别是否是静默更新
     */
    public void jsmethod_checkModuleVersion(UZModuleContext context) {
        JSONArray moduleBean = context.optJSONArray("MODULE_INFO");
        String upDataMessage = context.optString("upgradeMessage");
        boolean silentUpdate = context.optBoolean("silentUpdate");
        List<ModuleBean> list = new ArrayList<>();
        for (int i = 0; i < moduleBean.length(); i++) {
            try {
                JSONObject bean = moduleBean.getJSONObject(i);
                String name = bean.getString(Constants.MODULE_NAME);
                String version = bean.getString(Constants.MODULE_VERSION);
                String packageID = bean.getString(Constants.MODULE_PACKAGE_ID);
                String downloadURL = bean.getString(Constants.MODULE_DOWNLOAD_URL);
                version = version.replaceAll("\\.", "");
                switch (name) {
                    case Constants.MODULE_TEST:
                        String moduleVersion = DownLoadUtils.getInstance().getAPKVersion(context.getContext(), Constants.MODULE_TEST_APK,
                                "com.jzb.plugintestapk.Constants");
                        moduleVersion = moduleVersion.replaceAll("\\.", "");
                        if (Integer.parseInt(version) > Integer.parseInt(moduleVersion)) {
                            // 模块有新版本
                            list.add(new ModuleBean(name, Constants.MODULE_TEST_APK, version, downloadURL, packageID));
                        }
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 展示更新弹框
        DownLoadUtils.getInstance().showUpgradeView(context.getContext(), list, upDataMessage, silentUpdate);
    }
}
