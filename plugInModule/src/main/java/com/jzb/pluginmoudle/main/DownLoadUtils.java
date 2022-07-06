package com.jzb.pluginmoudle.main;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jzb.pluginmoudle.R;
import com.jzb.pluginmoudle.main.bean.ModuleBean;
import com.uzmap.pkg.uzkit.UZUtility;

import java.io.File;
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
    private File file;
    private long mRequestId;
    private DownloadManager mDownloadManager;
    private DownloadObserver mDownloadObserver;
    private ProgressBar progressBar;
    private Context context;

    private class DownloadObserver extends ContentObserver {
        private DownloadObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            queryDownloadStatus();
        }
    }

    public void startDownload(Context context, ProgressBar progressBar, String fileName, String filePath) {
        this.context = context;
        this.progressBar = progressBar;
        String corePath = Environment.getExternalStorageDirectory().getAbsolutePath();
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
                if (progressBar != null) {
                    progressBar.setProgress(progress);
                }
                if (DownloadManager.STATUS_SUCCESSFUL == status) {

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
    public void showUpgradeView(Context context, List<ModuleBean> list, String upDataMessage, boolean silentUpdate) {
        // 需要更新的文件列表
        if (list.size() != 0) {
            if (!silentUpdate) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                AlertDialog dialog;
                RelativeLayout dialogContentView = new RelativeLayout(context);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialogContentView.setLayoutParams(params);
                // 添加更新信息显示组件
                TextView textView = new TextView(context);
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UZUtility.dipToPix(50));
                params.setMargins(UZUtility.dipToPix(25), UZUtility.dipToPix(10), UZUtility.dipToPix(25), 0);
                textView.setLayoutParams(params);
                textView.setText(upDataMessage);
                textView.setTextSize(16);
                textView.setTextColor(Color.parseColor("#000000"));
                dialogContentView.addView(textView);
                // 更新按钮
                TextView tvPositive = new TextView(context);
                params = new RelativeLayout.LayoutParams(UZUtility.dipToPix(30), UZUtility.dipToPix(30));
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.topMargin = UZUtility.dipToPix(50);
                params.bottomMargin = UZUtility.dipToPix(20);
                params.rightMargin = UZUtility.dipToPix(30);
                tvPositive.setLayoutParams(params);
                tvPositive.setText("更新");
                tvPositive.setGravity(Gravity.CENTER);
                tvPositive.setTextSize(14);
                tvPositive.setTextColor(Color.parseColor("#0076F6"));
                // 取消按钮
                TextView tvNegative = new TextView(context);
                params = new RelativeLayout.LayoutParams(UZUtility.dipToPix(30), UZUtility.dipToPix(30));
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.topMargin = UZUtility.dipToPix(50);
                params.bottomMargin = UZUtility.dipToPix(20);
                params.rightMargin = UZUtility.dipToPix(100);
                tvNegative.setLayoutParams(params);
                tvNegative.setText("取消");
                tvNegative.setGravity(Gravity.CENTER);
                tvNegative.setTextSize(14);
                tvNegative.setTextColor(Color.parseColor("#000000"));
                // 进度条
                ProgressBar progressBar = new ProgressBar(context);

                // 添加组件
                dialogContentView.addView(tvPositive);
                dialogContentView.addView(tvNegative);
                builder.setView(dialogContentView)
                        .setTitle("更新提醒")
                        .create();
                dialog = builder.show();
                tvNegative.setOnClickListener(view -> dialog.dismiss());

            } else {
                // 静默更新直接下载模块
                DownLoadUtils.getInstance().startDownload(context, null, list.get(0).getName(),
                        list.get(0).getDownloadURL());
            }
        }
    }
}
