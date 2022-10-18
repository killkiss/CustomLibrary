package com.jzb.pluginmoudle.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jzb.pluginmoudle.main.bean.ModuleBean;
import com.jzb.pluginmoudle.main.utils.CallPopupWindowUtil;
import com.jzb.pluginmoudle.main.utils.DialPopupWindowUtil;
import com.jzb.pluginmoudle.main.utils.DownLoadUtils;
import com.jzb.pluginmoudle.main.utils.MeetingRequestHelper;
import com.jzb.pluginmoudle.main.utils.PopupWindowUtil;
import com.jzb.pluginmoudle.main.utils.RequestUtils;
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

    // 需要自检的APK模块(原则上所有引用模块需添加)
    private final String[] selfCheckApks = new String[]{Constants.MODULE_TEST_APK, Constants.MODULE_MEDIA_APK, Constants.MODULE_CALL_APK};
    // 对象
    public static UZModuleContext moduleContext;
    private String ext;

    public PlugInModule(UZWebView webView) {
        super(webView);
    }

    /**
     * 获取通话列表数据
     */
    public void jsmethod_getCallRecordData(UZModuleContext context) {
        String url = context.optString("url");
        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
        // 查询字段列表(传入则查询指定的字段,不传入则查询全部)
        object.put("fetchFields", null);
        object.put("page", 1);
        object.put("size", 100);
        object.put("count", true);
        object.put("unionId", context.optString("unionId"));
        object.put("token", context.optString("token"));
        RequestUtils.getInstance().handlerNetRequestPostString(context.getContext(), url, object, data -> {
            JSONObject ret = new JSONObject();
            try {
                ret.put("data", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            context.success(ret, true);
        });
    }

    /**
     * 加载插件模块的普通方法
     * 加载插件模块的Activity
     */
    public void jsmethod_loadPlugInModule(UZModuleContext context) {
        if (!selfCheck(context.getContext())) return;
        moduleContext = context;
        String apiType = context.optString("apiType");
        String className = context.optString("className");
        ext = context.optString("ext");
        String apiName = context.optString("apiName");
        // 文件名
        String fileName = context.optString("moduleName");
        // 获取模块插件位置
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
                if (fileName.equals("plugInTest.project") && className.equals("TestUtils")) {
                    localClass = pluginClassLoader.loadClass("com.jzb.plugintestapk.TestUtils");
                } else if (fileName.equals("error")) {
                    localClass = pluginClassLoader.loadClass("");
                }
                if (localClass == null) return;
                Constructor<?> localConstructor = localClass.getConstructor();
                Object instance = localConstructor.newInstance();
                // 获取方法
                Method method;
                if (fileName.equals("plugInTest.project")) {
                    // 获取全部参数后调用方法
                    switch (apiName) {
                        case "showData":
                            method = localClass.getMethod(apiName, Context.class);
                            method.setAccessible(true);
                            // 调用方法
                            method.invoke(instance, context.getContext());
                            break;
                        case "":
                            break;
                        default:
                            L.e("无效方法");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (apiType.equals("activity")) {
            // 如需使用到Activity需加载宿主Activity并实现全部生命周期
            Intent intent = new Intent(context.getContext(), PlugInFakerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("className", className);
            bundle.putString("fileName", fileName);
            bundle.putString("ext", ext);
            intent.putExtras(bundle);
            context.getContext().startActivity(intent);
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
                String moduleVersion;
                // 插件名
                switch (name) {
                    case Constants.MODULE_TEST:
                        // 插件的Constants类
                        moduleVersion = DownLoadUtils.getInstance().getAPKVersion(context.getContext(), Constants.MODULE_TEST_APK, Constants.MODULE_TEST_CONSTANTS);
                        moduleVersion = moduleVersion.replaceAll("\\.", "");
                        if (Integer.parseInt(version) > Integer.parseInt(moduleVersion)) {
                            // 模块有新版本
                            list.add(new ModuleBean(name, Constants.MODULE_TEST_APK, version, downloadURL, packageID));
                        }
                        break;
                    case Constants.MODULE_MEDIA_TEST:
                        moduleVersion = DownLoadUtils.getInstance().getAPKVersion(context.getContext(), Constants.MODULE_MEDIA_APK, Constants.MODULE_MEDIA_CONSTANTS);
                        moduleVersion = moduleVersion.replaceAll("\\.", "");
                        if (Integer.parseInt(version) > Integer.parseInt(moduleVersion)) {
                            // 模块有新版本
                            list.add(new ModuleBean(name, Constants.MODULE_MEDIA_APK, version, downloadURL, packageID));
                        }
                        break;
                    case Constants.MODULE_CALL_HISTORY:
                        moduleVersion = DownLoadUtils.getInstance().getAPKVersion(context.getContext(), Constants.MODULE_MEDIA_APK, Constants.MODULE_CALL_CONSTANTS);
                        moduleVersion = moduleVersion.replaceAll("\\.", "");
                        if (Integer.parseInt(version) > Integer.parseInt(moduleVersion)) {
                            // 模块有新版本
                            list.add(new ModuleBean(name, Constants.MODULE_CALL_APK, version, downloadURL, packageID));
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
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 没有授权，编写申请权限代码
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            return false;
        }
        long lastUpDateTime = DownLoadUtils.getInstance().getPackageLastUpdateTime(context);
        L.e("lastUpDateTime = " + lastUpDateTime);
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long saveLastUpDataTime = mSharedPreferences.getLong("saveLastUpDataTime", 0L);
        if (lastUpDateTime > saveLastUpDataTime) {
            // 如果是覆盖安装则删除本地apk包防止无法更新
            DownLoadUtils.getInstance().deleteLocalAPK(context, selfCheckApks);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putLong("saveLastUpDataTime", lastUpDateTime);
            editor.apply();
        }
        return true;
    }

    /**
     * 直接拨打电话 并保存通话记录
     */
    public void jsmethod_callPhone(UZModuleContext context) {
        try {
            String phoneNumber = context.optString("phoneNumber");
            if (ContextCompat.checkSelfPermission(context.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context.getContext(), new String[]{Manifest.permission.CALL_PHONE}, 100);
                return;
            }
            String textHint = context.optString("textHint");
            String url = context.optString("url");
            PopupWindowUtil.getInstance().showWindow((Activity) context.getContext(), textHint, new PopupWindowUtil.OnClickCallBack() {
                @Override
                public void onClickSure() {
                    if (!TextUtils.isEmpty(phoneNumber)) {
                        context.getContext().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber)));
                        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
                        object.put("token", context.optString("token"));
                        com.alibaba.fastjson.JSONObject ext = new com.alibaba.fastjson.JSONObject();
                        // 对方unionId(通话-接收人unionId)
                        ext.put("toId", context.optString("toId"));
                        ext.put("telType", "ORDINARY_CALL");
                        // 创建人unionId(通话-发起人unionId)
                        ext.put("creatorId", context.optString("unionId"));
                        ext.put("phone", context.optString("phoneNumber"));
                        // 对方unionId(通话-接收人unionId)
                        object.put("telRecord", ext);
                        RequestUtils.getInstance().handlerNetRequestPostString(context.getContext(), url, object, null);
                    }
                }

                @Override
                public void onClickCancel() {

                }
            });
        } catch (Exception e) {
            L.e(e.getMessage());
        }
    }

    /**
     * 展示拨号盘
     */
    public void jsmethod_showDialWindow(UZModuleContext context) {
        DialPopupWindowUtil.getInstance().showSearchWindow(context, data -> {
            JSONObject ret = new JSONObject();
            try {
                String[] dataArray = data.split(";");
                if (dataArray[0].equals("call_phone")) {
                    ret.put("event", "call_phone");
                    ret.put("value", dataArray[1]);
                } else {
                    ret.put("event", dataArray[0]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            context.success(ret, true);
        });
    }

    /**
     * 关闭拨号盘
     */
    public void jsmethod_disDialWindow(UZModuleContext context) {
        DialPopupWindowUtil.getInstance().disWindow();
    }

    /**
     * 展示通话选项
     */
    public void jsmethod_showCallWindow(UZModuleContext context) {
        CallPopupWindowUtil.getInstance().showSearchWindow(context, data -> {
            JSONObject ret = new JSONObject();
            try {
                ret.put("event", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            context.success(ret, true);
        });
    }

    /**
     * 关闭通话选项
     */
    public void jsmethod_disCallWindow(UZModuleContext context) {
        CallPopupWindowUtil.getInstance().disWindow();
    }

    /**
     * 关闭所有弹窗
     */
    public void jsmethod_disWindow(UZModuleContext context) {
        DialPopupWindowUtil.getInstance().disWindow();
        CallPopupWindowUtil.getInstance().disWindow();
        PopupWindowUtil.getInstance().disWindow();
    }

    /**
     * 通话弹窗
     */
    public void jsmethod_showCustomDialog(UZModuleContext context){
        PopupWindowUtil.getInstance().showWindow(context, new PopupWindowUtil.OnClickCallBack() {
            @Override
            public void onClickSure() {
                JSONObject ret = new JSONObject();
                try {
                    ret.put("event", "onClickSure");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                context.success(ret, true);
            }

            @Override
            public void onClickCancel() {

            }
        });
    }
}