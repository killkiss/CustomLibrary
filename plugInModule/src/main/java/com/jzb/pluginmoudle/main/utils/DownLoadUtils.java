package com.jzb.pluginmoudle.main.utils;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jzb.pluginmoudle.R;
import com.jzb.pluginmoudle.main.DexClassLoader;
import com.jzb.pluginmoudle.main.L;
import com.jzb.pluginmoudle.main.bean.ModuleBean;
import com.jzb.pluginmoudle.main.widget.ProgressView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;

/**
 * create：2022/7/4 17:03
 *
 * @author ykx
 * @version 1.0
 * @Description 插件APK下载模块
 */
public class DownLoadUtils {

    private static DownLoadUtils instance;
    private PopupWindow popupWindow;

    public static DownLoadUtils getInstance() {
        if (instance == null) {
            synchronized (DownLoadUtils.class) {
                if (instance == null) {
                    instance = new DownLoadUtils();
                }
            }
        }
        return instance;
    }

    /*********************************************下载相关*******************************************/
    private long mRequestId;
    private DownloadManager mDownloadManager;
    private DownloadObserver mDownloadObserver;
    private ProgressView progressView;
    // 显示总下载进度的控件
    private TextView tvProgress;
    private Context context;
    private String fileName;
    private String filePath;
    private String corePath;
    // 需要下载的模块数量
    private int countDownloadAPK = 0;
    // 当前下载的模块位置
    private int currentDownloadAPK = 0;
    // 下载地址集合
    private List<ModuleBean> list;

    private class DownloadObserver extends ContentObserver {
        private DownloadObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            queryDownloadStatus();
        }
    }

    public void startDownload(Context context, ProgressView progressView, TextView tvProgress, List<ModuleBean> list) {
        this.list = list;
        this.countDownloadAPK = list.size();
        this.currentDownloadAPK = 1;
        this.fileName = list.get(currentDownloadAPK - 1).getApkName();
        this.filePath = list.get(currentDownloadAPK - 1).getDownloadURL();
        this.context = context;
        this.progressView = progressView;
        this.tvProgress = tvProgress;
        if (list.size() == 1) {
            tvProgress.setVisibility(View.GONE);
        } else {
            tvProgress.setText(currentDownloadAPK + "/" + countDownloadAPK);
        }
        corePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        corePath = corePath + "/Download";
        File file = new File(corePath);
        // 判断文件夹是否存在 不存在则创建
        if (!file.exists()) L.e(file.mkdirs() + " 状态");
        // 查询文件是否已经存在 存在就删除重新下载
        file = new File(corePath + "/" + fileName);
        if (file.exists()) L.e(file.delete() + " 状态");
        mDownloadObserver = new DownloadObserver(new Handler());
        context.getContentResolver().registerContentObserver(
                Uri.parse("content://downloads/my_downloads"), true,
                mDownloadObserver);
        mDownloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        //将含有中文的url进行encode
        String fileUrl = toUtf8String(filePath);
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            mRequestId = mDownloadManager.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void queryDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(mRequestId);
        try (Cursor cursor = mDownloadManager.query(query)) {
            if (cursor != null && cursor.moveToFirst()) {
                // 已经下载的字节数
                long currentBytes = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                // 总需下载的字节数
                long totalBytes = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                // 状态所在的列索引
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                // 将当前下载的字节数转化为进度位置
                int progress = (int) ((currentBytes * 1.0) / totalBytes * 100);
                if (progressView != null) {
                    progressView.setProgress(progress);
                }
                if (DownloadManager.STATUS_SUCCESSFUL == status && progressView != null) {
                    progressView.setProgress(100);
                    File cacheFile = new File(context.getExternalCacheDir(), fileName);
                    // 删除旧文件
                    if (cacheFile.exists()) {
                        cacheFile.delete();
                    }
                    try {
                        File file = new File(corePath, fileName);
                        try (InputStream inputStream = new FileInputStream(file)) {
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
                    // 删除download目录的文件
                    File file = new File(corePath, fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    if (countDownloadAPK == currentDownloadAPK) {
                        progressView.setVisibility(View.GONE);
                        progressView = null;
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                            Toast.makeText(context, "安装完成", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // 进行下一个下载
                        currentDownloadAPK++;
                        // 下载进度归零
                        if (progressView != null) {
                            progressView.setProgress(0);
                        }
                        tvProgress.setText(currentDownloadAPK + "/" + countDownloadAPK);
                        // 设置文件名和新的下载地址
                        this.fileName = list.get(currentDownloadAPK - 1).getApkName();
                        this.filePath = list.get(currentDownloadAPK - 1).getDownloadURL();
                        // 查询文件是否已经存在 存在就删除重新下载
                        file = new File(corePath + "/" + fileName);
                        if (file.exists()) L.e(file.delete() + " 状态");
                        mDownloadObserver = new DownloadObserver(new Handler());
                        context.getContentResolver().registerContentObserver(
                                Uri.parse("content://downloads/my_downloads"), true,
                                mDownloadObserver);
                        mDownloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                        //将含有中文的url进行encode
                        String fileUrl = toUtf8String(filePath);
                        try {
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                            mRequestId = mDownloadManager.enqueue(request);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 将字节数转换为KB、MB、GB
     *
     * @param size 字节大小
     */
    private String formatKMGByBytes(long size) {
        StringBuilder bytes = new StringBuilder();
        DecimalFormat format = new DecimalFormat("###.00");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("GB");
        } else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("MB");
        } else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        } else {
            if (size <= 0) {
                bytes.append("0B");
            } else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }

    private String toUtf8String(String url) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < url.length(); i++) {
            char c = url.charAt(i);
            if (c <= 255) {
                sb.append(c);
            } else {
                byte[] b;
                try {
                    b = String.valueOf(c).getBytes("utf-8");
                } catch (Exception ex) {
                    System.out.println(ex);
                    b = new byte[0];
                }
                for (int value : b) {
                    int k = value;
                    if (k < 0)
                        k += 256;
                    sb.append("%").append(Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }

    public void stopDownLoad() {
        if (mDownloadObserver != null) {
            context.getContentResolver().unregisterContentObserver(mDownloadObserver);
        }
        if (mDownloadManager != null) {
            mDownloadManager.remove(mRequestId);
        }
    }

    /*************************其他方法******************************/
    /**
     * 获取或设置apk版本号
     *
     * @param fileName    apk名字
     * @param packageName 包名
     * @param context     上下文
     * @return 版本号
     */
    public String getAPKVersion(Context context, String fileName, String packageName) {
        // 获取测试模块插件位置
        File testPlugInPath = DexClassLoader.getAssetsCacheFile(context, fileName);
        ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
        String dexPath = new File(testPlugInPath.getAbsolutePath(), fileName).getAbsolutePath();
        File optimizedDirectory = new File(testPlugInPath, "pluginlib").getAbsoluteFile();
        File librarySearchPath = new File(testPlugInPath, "dexout");
        // 加载apk/jar文件
        DexClassLoader pluginClassLoader = new DexClassLoader(dexPath, optimizedDirectory, librarySearchPath.getAbsolutePath(), localClassLoader);
        // 普通方法
        try {
            // 获取需要调用的类名
            Class<?> localClass;
            localClass = pluginClassLoader.loadClass(packageName);
            Constructor<?> localConstructor = localClass.getConstructor();
            Object instance = localConstructor.newInstance();
            // 获取方法
            Method method = localClass.getMethod("getModuleTestVersion");
            method.setAccessible(true);
            // 调用方法
            return (String) method.invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.0.0";
    }

    /**
     * 展示更新弹框
     *
     * @param context       对象
     * @param list          更新模块列表
     * @param upDataMessage 更新内容
     * @param silentUpdate  是否静默更新
     */
    public void showUpgradeView(Activity context, List<ModuleBean> list, String upDataMessage, boolean silentUpdate) {
        // 需要更新的文件列表
        if (list.size() != 0) {
            if (!silentUpdate) {
                // 创建弹窗
                popupWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                // 创建内容
                View dialogView = LayoutInflater.from(context).inflate(R.layout.plugin_download_dialog, null);
                dialogView.setFocusable(true);
                dialogView.setFocusableInTouchMode(true);
                dialogView.setOnKeyListener((arg0, arg1, arg2) -> {
                    if (arg1 == KeyEvent.KEYCODE_BACK) {
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                        }
                    }
                    return false;
                });
                TextView tvMessage = dialogView.findViewById(R.id.tv_msg);
                tvMessage.setText(upDataMessage);
                // 填充内容
                popupWindow.setContentView(dialogView);
                popupWindow.setClippingEnabled(false);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(null);
                dialogView.findViewById(R.id.tv_btn1).setOnClickListener(view -> popupWindow.dismiss());
                // 忽略版本
                dialogView.findViewById(R.id.iv_close).setOnClickListener(view -> {
                    stopDownLoad();
                    popupWindow.dismiss();
                });
                // 停止安装
                dialogView.findViewById(R.id.tv_stop_install).setOnClickListener(v -> {
                    stopDownLoad();
                    popupWindow.dismiss();
                });
                dialogView.findViewById(R.id.tv_btn2).setOnClickListener(view -> {
                    // 开始更新
                    dialogView.findViewById(R.id.ll_progress).setVisibility(View.VISIBLE);
                    tvMessage.setVisibility(View.GONE);
                    dialogView.findViewById(R.id.linear_button1).setVisibility(View.GONE);
                    dialogView.findViewById(R.id.linear_button2).setVisibility(View.VISIBLE);
                    DownLoadUtils.getInstance().startDownload(context, dialogView.findViewById(R.id.progressView),
                            dialogView.findViewById(R.id.tv_progress), list);
                });
                popupWindow.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            } else {
                // 静默更新直接下载模块
                DownLoadUtils.getInstance().startDownload(context, new ProgressView(context), new TextView(context), list);
            }
        }
    }

    /**
     * 判断是否是覆盖安装
     **/
    public boolean isFirstInstall(Context context) {
        return getPackageFirstInstallTime(context) == getPackageLastUpdateTime(context);
    }

    public long getPackageFirstInstallTime(Context context) {
        String name = context.getPackageName();
        long time = 0;
        try {
            time = context.getPackageManager().getPackageInfo(name, 0).firstInstallTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public long getPackageLastUpdateTime(Context context) {
        String name = context.getPackageName();
        long time = 0;
        try {
            time = context.getPackageManager().getPackageInfo(name, 0).lastUpdateTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 删除指定APK
     *
     * @param apks apk名
     */
    public void deleteLocalAPK(Context context, String[] apks) {
        for (String apk : apks) {
            File cacheFile = new File(context.getExternalCacheDir(), apk);
            if (cacheFile.exists()) {
                cacheFile.delete();
                Log.e("-main-", "删除");
            }
        }
    }
}
