package com.jzb.pluginmoudle.main;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import dalvik.system.BaseDexClassLoader;

/**
 * create：2022/6/28 11:26
 *
 * @author ykx
 * @version 1.0
 * @Description 加载插件类
 */
public class DexClassLoader extends BaseDexClassLoader {

    public static final String EXTRA_DEX_PATH = "extra.dex.path";
    public static final String EXTRA_CLASS = "extra.class";


    /**
     * @param dexPath 需要加载的 dex / apk / jar 文件路径
     * @param optimizedDirectory dex 优化后存放的位置
     * @param librarySearchPath native 依赖的位置
     * @param parent 就是父类加载器
     */
    public DexClassLoader(String dexPath, File optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
    }

    /**
     * 将assets中的文件存放之内存
     * @param context 上下文
     * @param fileName 文件名
     * @return 存放位置
     */
    public static File getAssetsCacheFile(Context context, String fileName) {
        // 检测加载类是否为最新版本
        // 更新加载类文件
        // 如不需要更新包 并且第一次加载 则加载assets中的默认文件
        File cacheFile = new File(context.getExternalCacheDir(), fileName);
        try {
            try (InputStream inputStream = context.getAssets().open(fileName)) {
                try (FileOutputStream outputStream = new FileOutputStream(cacheFile)) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cacheFile.getParentFile();
    }

}
