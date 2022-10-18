package com.jzb.pluginmoudle.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jzb.pluginmoudle.main.utils.BrandUtil;
import com.jzb.pluginmoudle.main.utils.MeetingRequestHelper;
import com.jzb.pluginmoudle.main.utils.RequestUtils;

import org.json.JSONException;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * create：2022/6/29 14:26
 *
 * @author ykx
 * @version 1.0
 * @Description 加载插件模块的Activity (研究中)
 */
public class PlugInFakerActivity extends Activity {

    private DexClassLoader mDexClassLoader;
    private String activityName;
    private String ext;
    // apk名称
    private String fileName;
    private Resources mPluginResources;
    private AssetManager mPluginAssetManager;
    private String dexPath;
    // 宿主回调广播
    private BroadcastReceiver mAReceiver;
    private static PlugInFakerActivity instance;
    private Class<?> localClass;

    /**
     * 加载类
     */
    private Class<?> initClass() throws ClassNotFoundException {
        switch (activityName) {
            case "TestActivity":
                return mDexClassLoader.loadClass("com.jzb.plugintestapk.TestActivity");
            case Constants.MODULE_MEDIA_ACTIVITY:
                return mDexClassLoader.loadClass("com.jzb.media.activity.VideoPlayActivity");
            case Constants.MODULE_CALL_ACTIVITY:
                return mDexClassLoader.loadClass("com.jzb.jzbcallhistory.activity.CallHistoryActivity");
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handlerIntentData();
        initCallBackBroadCast();
        // 获取模块插件位置
        File testPlugInPath = DexClassLoader.getAssetsCacheFile(this, fileName);
        ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
        dexPath = new File(testPlugInPath.getAbsolutePath(), fileName).getAbsolutePath();
        File optimizedDirectory = new File(testPlugInPath, "pluginlib").getAbsoluteFile();
        File librarySearchPath = new File(testPlugInPath, "dexout");
        // 加载拓展APK资源数据
        handleResources(this);
        // 加载apk/jar文件
        mDexClassLoader = new DexClassLoader(dexPath, optimizedDirectory, librarySearchPath.getAbsolutePath(), localClassLoader);
        try {
            localClass = initClass();
            if (localClass != null) {
                Constructor<?> localConstructor = localClass.getConstructor();
                Object mInstance = localConstructor.newInstance();
                Method method = localClass.getMethod("onCreate", Activity.class, Bundle.class);
                Bundle intoBundler = new Bundle();
                if (activityName.equals(Constants.MODULE_CALL_ACTIVITY)) {
                    JSONObject object = JSONObject.parseObject(ext);
                    intoBundler.putString("ext", object.getString("callRecordListData"));
                    intoBundler.putString("unionId", object.getString("unionId"));
                    intoBundler.putString("token", object.getString("token"));
                }
                method.invoke(mInstance, PlugInFakerActivity.this, intoBundler);
            }
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();// 获取目标异常
            L.e(t.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        instance = this;
    }

    @Override
    public Resources getResources() {
        return mPluginResources != null ? mPluginResources : super.getResources();
    }

    @Override
    public AssetManager getAssets() {
        return mPluginAssetManager != null ? mPluginAssetManager : super.getAssets();
    }

    @Override
    public ClassLoader getClassLoader() {
        return mDexClassLoader != null ? mDexClassLoader : super.getClassLoader();
    }

    public void getMethod(String methodName, Object... objects) {
        try {
            if (localClass == null) {
                localClass = initClass();
            }
            if (localClass == null) return;
            Constructor<?> localConstructor = localClass.getConstructor();
            localConstructor.setAccessible(true);
            Object mInstance = localConstructor.newInstance();
            // 获取方法
            Method[] declaredMethods = localClass.getMethods();
            for (Method declaredMethod : declaredMethods) {
                if (declaredMethod.getName().equals(methodName)) {
                    declaredMethod.setAccessible(true);
                    switch (methodName) {
                        case "onClick":
                            declaredMethod.invoke(mInstance, (View) objects[0]);
                            break;
                        case "onConfigurationChanged":
                            declaredMethod.invoke(mInstance, (Configuration) objects[0]);
                            break;
                        case "hostCallBack":
                            declaredMethod.invoke(mInstance, PlugInFakerActivity.this, (String) objects[0], (String) objects[1]);
                            break;
                        default:
                            declaredMethod.invoke(mInstance);
                            break;
                    }
                    return;
                }
            }
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();// 获取目标异常
            L.e(t.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建插件资源
     */
    private void handleResources(Context context) {
        // 首先通过反射生成 AssetManager 实例
        try {
            mPluginAssetManager = context.getAssets();
            // 然后调用其 addAssetPath 把插件的路径添加进去。
            String assetMethod = "addAssetPath";
            Method addAssetPathMethod = mPluginAssetManager.getClass().getMethod(assetMethod, String.class);
            addAssetPathMethod.invoke(mPluginAssetManager, dexPath);
            // 调用 Resources 构造函数生成实例
            mPluginResources = new Resources(mPluginAssetManager, super.getResources().getDisplayMetrics(), super.getResources().getConfiguration());
            Configuration configuration = new Configuration();
            configuration.setToDefaults();
            mPluginResources.updateConfiguration(configuration, mPluginResources.getDisplayMetrics());
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMethod("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMethod("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        getMethod("onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getMethod("onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        getMethod("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (PlugInModule.moduleContext != null) {
            org.json.JSONObject ret = new org.json.JSONObject();
            try {
                // 通知前端页面已经销毁
                ret.put("message", "onDestroy");
                PlugInModule.moduleContext.success(ret, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        instance = null;
        getMethod("onDestroy");
        if (mAReceiver != null) {
            unregisterReceiver(mAReceiver);
            mAReceiver = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getMethod("onBackPressed");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getMethod("onConfigurationChanged", newConfig);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void handlerIntentData() {
        Intent intent = getIntent();
        if (intent == null) return;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            activityName = bundle.getString("className");
            fileName = bundle.getString("fileName");
            ext = bundle.getString("ext");
        }
    }

    /**
     * 初始化网络请求回调
     */
    private void initCallBackBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("PlugInFakerActivity");
        mAReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 获取Intent数据
                String requestAPI = intent.getStringExtra("requestAPI");
                String intentExt = intent.getStringExtra("ext");
                // 解析ext获取接口
                JSONObject object = JSON.parseObject(ext);
                String url = object.getString(requestAPI);
                // 解析intentExt
                JSONObject intentObject = JSON.parseObject(intentExt);
                intentObject.put("userId", object.getString("userId"));
                if (TextUtils.isEmpty(intentObject.getString("unionId"))) {
                    intentObject.put("unionId", object.getString("unionId"));
                }
                intentObject.put("token", object.getString("token"));
                intentObject.put("comId", object.getString("comId"));
                boolean isMeeting = MeetingRequestHelper.handlerRequest(context, requestAPI, url, intentObject, data ->
                        getMethod("hostCallBack", requestAPI, data));
                if (!isMeeting) {
                    if (requestAPI.equals("GET_USER_LIST_BY_DEPTID")) {
                        // 解析intentExt
                        object.put("keywords", intentObject.getString("keywords"));
                        RequestUtils.getInstance().handlerNetRequestPostString(context, url, object, data ->
                                        getMethod("hostCallBack", requestAPI, data));
                    } else if (requestAPI.equals("CALL_AUDIO") || requestAPI.equals("CALL_VIDEO")) {
                        // 判断是否是小米，如果是则申请权限
                        if (BrandUtil.isBrandXiaoMi()) {
                            if (!BrandUtil.hasPermissionBackRun(context)) {
                                return;
                            }
                        }
                        org.json.JSONObject ret = new org.json.JSONObject();
                        try {
                            ret.put("type", requestAPI);
                            ret.put("callNickName", intentObject.getString("callNickName"));
                            ret.put("calleeUserid", intentObject.getString("calleeUserid"));
                            ret.put("toId", intentObject.getString("toId"));
                            ret.put("telType", intentObject.getString("telType"));
                            ret.put("phoneNumber", intentObject.getString("phoneNumber"));
                            ret.put("creatorId", intentObject.getString("creatorId"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        PlugInModule.moduleContext.success(ret, false);
                    }
                }
            }
        };
        registerReceiver(mAReceiver, filter);
    }

    public static PlugInFakerActivity getInstance() {
        return instance;
    }
}