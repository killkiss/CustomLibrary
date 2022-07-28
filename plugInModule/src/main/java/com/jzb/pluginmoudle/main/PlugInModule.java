package com.jzb.pluginmoudle.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.jzb.pluginmoudle.main.bean.ModuleBean;
import com.jzb.pluginmoudle.main.utils.DownLoadUtils;
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
        if (!selfCheck(context.getContext())) return;
        // 是否使用测试模块插件
        boolean useTestModule = context.optBoolean("useTestModule");
        String apiType = context.optString("apiType");
        String className = context.optString("className");
        String apiName = context.optString("apiName");
        if (useTestModule) {
            // 文件名
            String fileName = "plugInTest.project";
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
                    Class<?> localClass = null;
                    if (className.equals("TestUtils")) {
                        localClass = pluginClassLoader.loadClass("com.jzb.plugintestapk.TestUtils");
                    }
                    if (localClass == null) return;
                    Constructor<?> localConstructor = localClass.getConstructor();
                    Object instance = localConstructor.newInstance();
                    // 获取方法
                    Method method;
                    switch (apiName) {
                        case "showData":
                            // 获取全部参数后调用方法
                            method = localClass.getMethod(apiName, Context.class);
                            method.setAccessible(true);
                            // 调用方法
                            method.invoke(instance, context.getContext());
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (apiType.equals("activity")) {
                // 如需使用到Activity需加载宿主Activity并实现全部生命周期
                Intent intent = new Intent(context.getContext(), PlugInFakerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("className", className);
                intent.putExtras(bundle);
                context.getContext().startActivity(intent);
            }
        }
    }

    /*********** 1.请直接在switch中添加需要检测的模块 2.自检方法中加入被检测模块 ********/

    /**
     * 获取模块版本号
     * context 前端对象
     * MODULE_NAME:模块名
     * MODULE_VERSION:模块版本
     * upgradeMessage:版本更新文本内容
     * silentUpdate:判别是否是静默更新
     */
    public void jsmethod_checkModuleVersion(UZModuleContext context) {
        if (!selfCheck(context.getContext())) return;
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
                // 插件名
                switch (name) {
                    case Constants.MODULE_TEST:
                        // 插件的Constants类
                        String moduleVersion = DownLoadUtils.getInstance().getAPKVersion(context.getContext(), Constants.MODULE_TEST_APK,
                                Constants.MODULE_TEST_CONSTANTS);
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
        DownLoadUtils.getInstance().showUpgradeView((Activity) context.getContext(), list, upDataMessage, silentUpdate);
    }

    /**
     * 自检
     *
     * @return 是否通过
     */
    private boolean selfCheck(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 没有授权，编写申请权限代码
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            return false;
        }
        long lastUpDateTime = DownLoadUtils.getInstance().getPackageLastUpdateTime(context);
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long saveLastUpDataTime = mSharedPreferences.getLong("saveLastUpDataTime", 0L);
        if (lastUpDateTime > saveLastUpDataTime) {
            // 如果是覆盖安装则删除本地apk包防止无法更新
            DownLoadUtils.getInstance().deleteLocalAPK(context, new String[]{"plugInTest.project"});
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putLong("saveLastUpDataTime", lastUpDateTime);
            editor.apply();
        }
        return true;
    }
}
