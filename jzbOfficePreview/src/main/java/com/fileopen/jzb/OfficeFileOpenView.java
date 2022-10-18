package com.fileopen.jzb;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.fileopen.jzb.OfficeFileOpenModule.dip2px;
import static com.fileopen.jzb.OfficeFileOpenModule.initX5Success;
import static com.fileopen.jzb.OfficeFileOpenModule.isInitTBS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * create：2022/4/28 9:15
 *
 * @author ykx
 * @version 1.0
 * @Description 基础文件类型预览view
 */
@SuppressLint("SetTextI18n")
public class OfficeFileOpenView extends RelativeLayout implements TbsListener {

    // 文件预览view
    private TbsReaderView tbsReaderView;
    private RelativeLayout loadingView;
    private final Context context;
    private String filePath;
    // 内核下载地址
    private String coreDownLoadPath;
    private String coreInstallPath;
    private String fileName;
    private boolean isLocalFile = true;
    private View viewHead;
    // 静态和在线安装状态切换
    private final boolean CusServiceLoadMode = true;
    // 自动下载安装
    private boolean autoDownLoad = true;
    // 是否需要本地保存
    private boolean allowCache = false;
    // 预览工具版本
    private final String fileOpenVersion = "tbs_core_046011";

    public OfficeFileOpenView(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 初始化view并添加预览模块
     */
    public OfficeFileOpenView initAndAddTbsReaderView() {
        // 添加用于展示的视窗
        tbsReaderView = new TbsReaderView(context, (integer, o, o1) -> {
            if (integer == TbsReaderView.ReaderCallback.SEARCH_SELECT_TEXT) {
                // 搜索按钮
                Uri uri = null;
                try {
                    uri = Uri.parse("http://www.baidu.com/s?&ie=utf-8&oe=UTF-8&wd=" + URLEncoder.encode(o.toString(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                final Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(uri);
                context.startActivity(intent);
            }
        });
        addView(tbsReaderView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        addStartDownload();
        return this;
    }

    public OfficeFileOpenView addHeadView(View viewHead) {
        this.viewHead = viewHead;
        return this;
    }

    private String otherName;

    /**
     * 设置预览文件地址
     *
     * @param filePath  文件地址
     * @param otherName 文件名
     */
    public OfficeFileOpenView setFilePath(String filePath, String otherName) {
        if (filePath.contains("[")) {
            // 如果是数组的情况则需要额外处理
            filePath = filePath.replaceAll("\\[", "");
            filePath = filePath.replaceAll("]", "");
            String[] paths = filePath.split(",");
            filePath = paths[0];
            filePath = filePath.replaceAll("\"", "");
        }
        this.filePath = filePath;
        this.otherName = otherName;
        return this;
    }

    /**
     * 是否允许本地保存
     *
     * @param allowCache 是否允许
     */
    public OfficeFileOpenView setAllowCache(boolean allowCache) {
        this.allowCache = allowCache;
        return this;
    }

    /**
     * 初始化x5内核
     */
    private void initX5() {
        if (!CusServiceLoadMode) {
            initX5First();
        } else {
            QbSdk.setTbsListener(this);
            boolean canLoadX5 = QbSdk.canLoadX5(context);
            if (canLoadX5) {
                // 第一次加载需要进行初始化否则第一次预览显示不了
                if (!isInitTBS && !initX5Success) {
                    QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
                        @Override
                        public void onCoreInitFinished() {
                        }

                        @Override
                        public void onViewInitFinished(boolean b) {
                            // 初始化完成
                            isInitTBS = true;
                            initX5Success = true;
                            displayFile();
                        }
                    });
                } else {
                    displayFile();
                }
            } else {
                new AlertDialog.Builder(context)
                        .setTitle("安装提醒")
                        .setMessage("首次预览需加载预览模块，预计耗时1分钟")
                        .setPositiveButton("下载", (dialogInterface, i) -> {
                            // 用于适配Android12的内核 暂时不更新
                            coreDownLoadPath = "http://cdnpro.jizhibao.com.cn/yapi-doc/" + fileOpenVersion;
                            startDownloadCore();
                        })
                        .setNegativeButton("取消", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            if (callBack != null) {
                                callBack.cancel();
                            }
                        })
                        .create()
                        .show();

            }
        }
    }

    private void initX5First() {
        // 初始化TBS
        // 在调用TBS初始化、创建WebView之前进行如下配置
        QbSdk.reset(context);
        HashMap<String, Object> map = new HashMap<>();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        QbSdk.setTbsListener(OfficeFileOpenView.this);
        QbSdk.setDownloadWithoutWifi(true);//非wifi条件下允许下载X5内核
        //x5内核初始化接口
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean success) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，
                if (success) {
                    displayFile();
                } else {
                    new AlertDialog.Builder(context)
                            .setTitle("安装提醒")
                            .setMessage("首次预览需加载预览模块，预计耗时1分钟")
                            .setPositiveButton("下载", (dialogInterface, i) -> {
                                autoDownLoad = false;
                                QbSdk.reset(context);
                                QbSdk.setDownloadWithoutWifi(true);
                                QbSdk.setTbsListener(OfficeFileOpenView.this);
                                TbsDownloader.startDownload(context);
                            })
                            .setNegativeButton("取消", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                if (callBack != null) {
                                    callBack.cancel();
                                }
                            })
                            .create()
                            .show();
                }
                initX5Success = success;
                isInitTBS = true;
            }

            @Override
            public void onCoreInitFinished() {
            }
        });
    }

    public OfficeFileOpenView initClickCallBack(ClickCallBack callBack) {
        this.callBack = callBack;
        return this;
    }

    /**
     * 组件完成开始预览展示
     */
    public void build() {
        if (isInitTBS) {
            if (initX5Success) {
                displayFile();
            } else {
                new AlertDialog.Builder(context)
                        .setTitle("安装提醒")
                        .setMessage("首次预览需加载预览模块，预计耗时1分钟")
                        .setPositiveButton("下载", (dialogInterface, i) -> {
                            autoDownLoad = false;
                            QbSdk.reset(context);
                            QbSdk.setTbsListener(OfficeFileOpenView.this);
                            TbsDownloader.startDownload(context);
                        })
                        .setNegativeButton("取消", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            if (callBack != null) {
                                callBack.cancel();
                            }
                        })
                        .create()
                        .show();
            }
        } else {
            initX5();
        }
    }

    // 预览文件
    private void displayFile() {
        fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        if (filePath.contains("http")) {
            isLocalFile = false;
            if (!fileName.contains(".")) {
                fileName = fileName + otherName.substring(otherName.lastIndexOf("."));
            }
            // 判断文件是否存在，如存在则已经完成下载
            cacheFile = new File(context.getExternalCacheDir(), fileName);
            String basePath = Environment.getExternalStorageDirectory().getPath() + "/Download";
            File file = new File(basePath + "/" + fileName);
            if (cacheFile.exists()) {
                filePath = cacheFile.getAbsolutePath();
                localOpenFile(false);
            } else {
                // 如果没有Download文件夹需要先创建
                if (!file.getParentFile().exists()) {
                    //父目录不存在 创建父目录
                    if (!file.getParentFile().mkdirs()) {
                        return;
                    }
                }
                // 网络下载
                startDownload();
            }
        } else {
            isLocalFile = true;
            localOpenFile(true);
        }
    }

    private void localOpenFile(boolean needCopy) {
        if (needCopy) {
            if (!fileName.contains(".")) {
                String name = filePath.substring(filePath.lastIndexOf("/"));
                filePath = getCacheFile(context, filePath, name + otherName.substring(otherName.lastIndexOf(".")));
            } else {
                filePath = getCacheFile(context, filePath, filePath.substring(filePath.lastIndexOf("/")));
            }
        }
        hideLoading();
        Bundle bundle = new Bundle();
        bundle.putString("filePath", filePath);
        bundle.putString("tempPath", filePath.substring(0, filePath.lastIndexOf("/")));
        if (callBack != null) {
            callBack.callBackMessage(filePath);
        }
        String fileType = filePath.substring(filePath.lastIndexOf(".") + 1);
        boolean result = tbsReaderView.preOpen(fileType, false);
        if (result) {
            if (viewHead != null) {
                viewHead.setVisibility(VISIBLE);
            }
            tbsReaderView.openFile(bundle);
        }
    }

    /**
     * 开始下载
     */
    private TextView downloadingHint;

    private void addStartDownload() {

        loadingView = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dip2px(context, 274), dip2px(context, 161));
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        loadingView.setLayoutParams(params);
        loadingView.setBackgroundResource(R.drawable.office_loading_shape);
        loadingView.setGravity(LinearLayout.VERTICAL);
        loadingView.setOnClickListener(view -> L.e(""));

        // 添加加载效果
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setId(R.id.office_preview_loading_view);
        params = new RelativeLayout.LayoutParams(dip2px(context, 37), dip2px(context, 37));
        params.topMargin = dip2px(context, 25);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        progressBar.setLayoutParams(params);
        loadingView.addView(progressBar);

        // 添加文字进度描述
        downloadingHint = new TextView(context);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dip2px(context, 15);
        params.addRule(RelativeLayout.BELOW, R.id.office_preview_loading_view);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        downloadingHint.setTextSize(14);
        downloadingHint.setTextColor(Color.parseColor("#000000"));
        downloadingHint.setLayoutParams(params);
        loadingView.addView(downloadingHint);
        downloadingHint.setText("加载配置中请稍等");

        // 添加取消按钮
        TextView txtCancel = new TextView(context);
        txtCancel.setText("取消");
        txtCancel.setTextColor(Color.parseColor("#1989FA"));
        txtCancel.setTextSize(14);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dip2px(context, 55);
        params.addRule(RelativeLayout.BELOW, R.id.office_preview_loading_view);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        txtCancel.setPadding(10, 10, 10, 10);
        txtCancel.setLayoutParams(params);
        loadingView.addView(txtCancel);
        txtCancel.setOnClickListener(view -> {
            if (mDownloadManager != null) {
                mDownloadManager.remove(mRequestId);
            }
            pauseDownloadAndCancel();
            if (callBack != null) {
                callBack.cancel();
            }
        });
        // 添加
        addView(loadingView);
    }

    public void pauseDownloadAndCancel() {
        QbSdk.pauseDownload();
    }

//    /**
//     * 静态安装文件获取
//     */
//    public static String getAssetsCacheFile(Context context, String fileName) {
//        File cacheFile = new File(context.getExternalCacheDir(), fileName);
//        try {
//            try (InputStream inputStream = context.getAssets().open(fileName)) {
//                try (FileOutputStream outputStream = new FileOutputStream(cacheFile)) {
//                    byte[] buf = new byte[1024];
//                    int len;
//                    while ((len = inputStream.read(buf)) > 0) {
//                        outputStream.write(buf, 0, len);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return cacheFile.getAbsolutePath();
//    }

    public static String getCacheFile(Context context, String oldUrl, String fileName) {
        File cacheFile = new File(context.getExternalCacheDir(), fileName);
        try {
            try (InputStream inputStream = new FileInputStream(oldUrl)) {
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
            return oldUrl;
        }
        return cacheFile.getAbsolutePath();
    }

    private ClickCallBack callBack;

    @Override
    public void onDownloadFinish(int i) {
        L.e(i + "onDownloadFinish");
    }

    @Override
    public void onInstallFinish(int i) {
        L.e(i + "onInstallFinish");
        if (i == 200) {
            if (downloadingHint != null) {
                ((Activity) context).runOnUiThread(() -> downloadingHint.setText("安装完成"));
                coreDownLoadPath = "";
            }
            initX5Success = true;
            isInitTBS = true;
            if (CusServiceLoadMode) ((Activity) context).runOnUiThread(this::displayFile);
            if (!autoDownLoad) {
                autoDownLoad = true;
                ((Activity) context).runOnUiThread(this::displayFile);
            }
        }
    }

    @Override
    public void onDownloadProgress(int i) {
        L.e(i + "onDownloadProgress");
        if (downloadingHint != null)
            ((Activity) context).runOnUiThread(() -> downloadingHint.setText("正在安装预览工具(" + i + "/100)"));
    }

    interface ClickCallBack {
        void cancel();

        void callBackMessage(String url);

        void abnormalUpload(String message);
    }

    /*********************************************下载相关*******************************************/
    private File cacheFile;
    private long mRequestId;
    private DownloadManager mDownloadManager;
    private DownloadObserver mDownloadObserver;

    private class DownloadObserver extends ContentObserver {
        private DownloadObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            queryDownloadStatus();
        }
    }

    private void startDownload() {
        try {
            String corePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            corePath = corePath + "/Download";
            File file = new File(corePath);
            // 判断文件夹是否存在 不存在则创建
            if (!file.exists()) L.e(file.mkdirs() + " 状态");
            // 查询文件是否已经存在 存在就删除重新下载
            file = new File(corePath + "/" + fileName);
            if (file.exists()) L.e(file.delete() + " 状态");
//        if (mDownloadObserver == null && mDownloadManager == null) {
            mDownloadObserver = new DownloadObserver(new Handler());
            context.getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, mDownloadObserver);
            mDownloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
//        }
            // 将含有中文的url进行encode
            String fileUrl = toUtf8String(filePath);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
            mRequestId = mDownloadManager.enqueue(request);
        } catch (Exception e) {
            if (callBack != null) {
                callBack.abnormalUpload(e.getMessage());
            }
        }
    }

    /**
     * 下载文件内核
     */
    private void startDownloadCore() {
        String corePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        corePath = corePath + "/Download";
        File file = new File(corePath);
        // 判断文件夹是否存在 不存在则创建
        if (!file.exists()) L.e(file.mkdirs() + " 状态");
        // 查询文件是否已经存在 存在就删除重新下载
        coreInstallPath = corePath + "/" + fileOpenVersion;
        file = new File(coreInstallPath);
        if (file.exists()) L.e(file.delete() + " 状态");
        mDownloadObserver = new DownloadObserver(new Handler());
        context.getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, mDownloadObserver);
        mDownloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        //将含有中文的url进行encode
        String fileUrl = toUtf8String(coreDownLoadPath);
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileOpenVersion);
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
                if (totalBytes == 0) {
                    // 代表下载不可用 直接取消下载
                    if (mDownloadManager != null) {
                        mDownloadManager.remove(mRequestId);
                    }
                    if (callBack != null) {
                        callBack.callBackMessage("");
                    }
                }
                // 状态所在的列索引
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (!TextUtils.isEmpty(coreDownLoadPath)) {
                    // 将当前下载的字节数转化为进度位置
                    int progress = (int) ((currentBytes * 1.0) / totalBytes * 100);
                    downloadingHint.setText("正在安装预览工具(" + progress + "/100)");
                    if (DownloadManager.STATUS_SUCCESSFUL == status && downloadingHint.getVisibility() == View.VISIBLE) {
                        // Android 12 不允许在其他位置安装apk 需要优化
                        File cacheFile = new File(context.getExternalCacheDir(), fileOpenVersion);
                        try {
                            File file = new File(coreInstallPath);
                            try (InputStream inputStream = new FileInputStream(file)) {
                                try (FileOutputStream outputStream = new FileOutputStream(cacheFile)) {
                                    byte[] buf = new byte[1024];
                                    int len;
                                    while ((len = inputStream.read(buf)) > 0) {
                                        outputStream.write(buf, 0, len);
                                    }
                                }
                            }
                            L.e(file.delete() + "");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        QbSdk.installLocalTbsCore(context, 45318, cacheFile.getAbsolutePath());
                    }
                } else {
                    downloadingHint.setText("载入文档中...(" + formatKMGByBytes(currentBytes) + "/" + formatKMGByBytes(totalBytes) + ")");
                    if (DownloadManager.STATUS_SUCCESSFUL == status && downloadingHint.getVisibility() == View.VISIBLE) {
                        // Android 12 不允许在其他位置安装apk 需要优化
                        File cacheFile = new File(context.getExternalCacheDir(), fileName);
                        try {
                            String corePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                            corePath = corePath + "/Download";
                            File file = new File(corePath + "/" + fileName);
                            try (InputStream inputStream = new FileInputStream(file)) {
                                try (FileOutputStream outputStream = new FileOutputStream(cacheFile)) {
                                    byte[] buf = new byte[1024];
                                    int len;
                                    while ((len = inputStream.read(buf)) > 0) {
                                        outputStream.write(buf, 0, len);
                                    }
                                }
                            }
                            file.delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        displayFile();
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
                    b = String.valueOf(c).getBytes(StandardCharsets.UTF_8);
                } catch (Exception ex) {
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

    private void hideLoading() {
        if (downloadingHint != null && loadingView != null) {
            loadingView.setVisibility(INVISIBLE);
            downloadingHint.setVisibility(INVISIBLE);
        }
    }

    /**
     * 释放文件预览资源
     */
    public void releaseView() {
        if (tbsReaderView != null) {
            tbsReaderView.onStop();
        }
        if (mDownloadObserver != null) {
            context.getContentResolver().unregisterContentObserver(mDownloadObserver);
        }
        if (!isLocalFile && cacheFile != null && cacheFile.exists() && !allowCache) {
            L.e(cacheFile.delete() + " = 文件删除状态");
        }
    }

}
