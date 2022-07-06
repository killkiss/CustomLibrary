package com.jzb.pluginmoudle.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;

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
    private Resources mPluginResources;
    private AssetManager mPluginAssetManager;
    private String dexPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 文件名
        String fileName = "plugInTest.apk";
        // 获取测试模块插件位置
        File testPlugInPath = DexClassLoader.getAssetsCacheFile(this, fileName);
        handlerIntentData();
        ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
        dexPath = new File(testPlugInPath.getAbsolutePath(), fileName).getAbsolutePath();
        File optimizedDirectory = new File(testPlugInPath, "pluginlib").getAbsoluteFile();
        File librarySearchPath = new File(testPlugInPath, "dexout");
        // 加载拓展APK资源数据
        handleResources(this);
        // 加载apk/jar文件
        mDexClassLoader = new DexClassLoader(dexPath, optimizedDirectory, librarySearchPath.getAbsolutePath(), localClassLoader);
        try {
            if (activityName.equals("TestActivity")) {
                Class<?> localClass = mDexClassLoader.loadClass("com.jzb.plugintestapk.TestActivity");
                Constructor<?> localConstructor = localClass.getConstructor();
                Object mInstance = localConstructor.newInstance();
                Method method = localClass.getMethod("onCreate", Activity.class, Bundle.class);
                method.invoke(mInstance, PlugInFakerActivity.this, bundle);
            }
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();// 获取目标异常
            L.e(t.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void getMethod(String methodName) {
        Class<?> localClass = null;
        try {
            if (activityName.equals("TestActivity")) {
                localClass = mDexClassLoader.loadClass("com.jzb.plugintestapk.TestActivity");
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
                    declaredMethod.invoke(mInstance);
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
            Method addAssetPathMethod = mPluginAssetManager.getClass()
                    .getMethod("addAssetPath", String.class);
            addAssetPathMethod.invoke(mPluginAssetManager, dexPath);
            // 调用 Resources 构造函数生成实例
            mPluginResources = new Resources(mPluginAssetManager,
                    super.getResources().getDisplayMetrics(),
                    super.getResources().getConfiguration());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
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
        getMethod("onDestroy");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private Bundle bundle;

    public void handlerIntentData() {
        Intent intent = getIntent();
        if (intent == null) return;
        bundle = intent.getExtras();
        if (bundle != null) {
            activityName = bundle.getString("apiName");
        }
    }
}
