package com.fileopen.jzb;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * create：2022/4/27 11:11
 *
 * @author ykx
 * @version 1.0
 * @Description 集成腾讯TBS文件预览功能
 * step1 : 预加载SDK
 * step2 : 加载失败后需要下载X5内核
 * step3 : 安装X5内核
 * step4 : 网络和本地打开office文件 （下载文件->下载插件->打开文档）
 * step5 : 关闭时需删除本地文件
 */
public class OfficeFileOpenModule extends UZModule {

    public static boolean isInitTBS = false;
    public static boolean initX5Success = false;
    private boolean isOpenFile = false;
    private OfficeFileOpenView officeFileOpenView;
    private LinearLayout linearLayout;
    private long lastClickTime = 0;

    public OfficeFileOpenModule(UZWebView webView) {
        super(webView);
    }

    public void jsmethod_openFile(UZModuleContext uzModuleContext) {
        Context context = uzModuleContext.getContext();
        // 禁止连点
        if (System.currentTimeMillis() - lastClickTime > 1000) {
            lastClickTime = System.currentTimeMillis();
        } else {
            return;
        }
        // 意外重复打开
        if (isOpenFile) {
            close();
        }
        int width = uzModuleContext.optInt("w");
        int height = uzModuleContext.optInt("h");
        String fileName = uzModuleContext.optString("fileName");
        if (width == 0) {
            width = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            width = dip2px(context, width);
        }
        if (height == 0) {
            height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            height = dip2px(context, height);
        }
        // 添加顶部标题（返回键加标题）
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        // 添加头部
        LinearLayout linearHead = new LinearLayout(context);
        linearHead.setOrientation(LinearLayout.HORIZONTAL);
        linearHead.setPadding(0, 0, 0, dip2px(context, 5));
        linearHead.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(context, 65)));
        linearHead.setBackgroundColor(Color.parseColor("#1989fa"));
        linearHead.setGravity(Gravity.BOTTOM);
        // 添加返回按钮
        ImageView imageBack = new ImageView(context);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(dip2px(context, 33), dip2px(context, 33));
        imgParams.leftMargin = dip2px(context, 10);
        imageBack.setLayoutParams(imgParams);
        imageBack.setImageResource(R.drawable.office_preivew_back);
        linearHead.addView(imageBack);
        imageBack.setOnClickListener(view -> close());
        // 添加标题
        TextView tvTitle = new TextView(context);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        tvTitle.setTextSize(18);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setSingleLine(true);
        tvTitle.setPadding(0, 0, 0, dip2px(context, 2));
        tvTitle.setEllipsize(TextUtils.TruncateAt.END);
        tvTitle.setTextColor(Color.parseColor("#FFFFFF"));
        tvTitle.setText(fileName);
        linearHead.addView(tvTitle);
        // 添加blank预留空间
        ImageView imageBlank = new ImageView(context);
        imgParams = new LinearLayout.LayoutParams(dip2px(context, 33), dip2px(context, 33));
        imgParams.rightMargin = dip2px(context, 10);
        imageBlank.setLayoutParams(imgParams);
        linearHead.addView(imageBlank);
        linearHead.setVisibility(View.GONE);
        linearLayout.addView(linearHead);
        // 展示用坐标
        officeFileOpenView = new OfficeFileOpenView(context());
        officeFileOpenView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        officeFileOpenView.setOnClickListener(view -> {});
        officeFileOpenView.setBackgroundColor(Color.parseColor("#33000000"));
        officeFileOpenView
                .initAndAddTbsReaderView()
                .setFilePath(uzModuleContext.optString("filePath"),fileName)
                .initClickCallBack(new OfficeFileOpenView.ClickCallBack() {
                    @Override
                    public void cancel() {
                        close();
                    }

                    @Override
                    public void callBackMessage(String url) {
                        // 返回地址给apiCloud
                        JSONObject ret = new JSONObject();
                        try {
                            ret.put("filePath", url);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        uzModuleContext.success(ret, true);
                    }
                })
                .build();
        linearLayout.addView(officeFileOpenView);
        int xAxis = uzModuleContext.optInt("x");
        int yAxis = uzModuleContext.optInt("y");
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rlp.leftMargin = xAxis;
        rlp.topMargin = yAxis;
        isOpenFile = true;
        linearLayout.setOnClickListener(view -> {
            if (officeFileOpenView != null) {
                officeFileOpenView.pauseDownloadAndCancel();
            }
            close();
        });
        insertViewToCurWindow(linearLayout, rlp);
    }

    /**
     * 安全关闭文件预览view
     */
    public void jsmethod_closeOfficeView(UZModuleContext uzModuleContext) {
        if (officeFileOpenView != null) {
            officeFileOpenView.pauseDownloadAndCancel();
        }
        close();
    }

    private void close() {
        isOpenFile = false;
        if (officeFileOpenView != null) {
            officeFileOpenView.releaseView();
        }
        removeViewFromCurWindow(linearLayout);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
