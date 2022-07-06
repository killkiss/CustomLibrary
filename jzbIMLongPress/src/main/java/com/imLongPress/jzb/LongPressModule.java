package com.imLongPress.jzb;

import static com.imLongPress.jzb.utls.PressDrawableHelper.dip2px;
import static com.imLongPress.jzb.utls.PressDrawableHelper.px2dip;
import static com.imLongPress.jzb.widget.LongPressView.arrowSize;
import static com.imLongPress.jzb.widget.LongPressView.singHeight;
import static com.imLongPress.jzb.widget.LongPressView.singWidth;

import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.imLongPress.jzb.widget.LongPressView;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * create：2022/4/13 16:23
 *
 * @author ykx
 * @version 1.0
 * @Description 页面长按弹窗工具栏
 */
public class LongPressModule extends UZModule {

    private LongPressView longPressView;
    private UZModuleContext moduleContext;

    public LongPressModule(UZWebView webView) {
        super(webView);
    }

    public void jsmethod_start(UZModuleContext moduleContext) {

    }

    /**
     * 显示工具栏弹窗
     */
    public void jsmethod_show(UZModuleContext moduleContext) {
        this.moduleContext = moduleContext;
        singHeight = dip2px(context(),60);
        singWidth = dip2px(context(),57);
        arrowSize = dip2px(context(),10);
        WindowManager windowManager = activity().getWindowManager();
        int scHeight = windowManager.getDefaultDisplay().getHeight();
        int scWidth = windowManager.getDefaultDisplay().getWidth();
        scWidth = px2dip(context(), scWidth);
        scHeight = px2dip(context(),scHeight);
        closeWindow();
        // 获取属性
        JSONObject style = moduleContext.optJSONObject("style");
        // 按钮集合
        JSONArray btnArray = moduleContext.optJSONArray("btnArray");
        // 设定弹框位置
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int x = moduleContext.optInt("centerX");
        int y = moduleContext.optInt("centerY");
        int h = moduleContext.optInt("height");
        int elemType = moduleContext.optInt("elemType");
        // 3类图片中长图需要h5那边处理
        if (elemType == 3 || elemType == 7) {
            h -= 20;
            y += 10;
        } else {
            h -= 5;
            y += 5;
        }
        int linearLeftOffset;
        int linearTopOffset;
        if (btnArray.length() != 0) {
            String icon = btnArray.optJSONObject(0).optString("icon");
            if (TextUtils.isEmpty(icon)) {
                // 减去图片占用高度
                singHeight = singHeight - dip2px(context(), 18);
            }
        } else {
            return;
        }
        // 如果弹窗箭头靠右则需要重新计算显示初始x
        int width, height;
        if (btnArray.length() < 4) {
            width = btnArray.length() * px2dip(context(), LongPressView.singWidth);
        } else {
            width = 4 * px2dip(context(), LongPressView.singWidth);
        }
        int reserveGap = 15;
        int overLeftWidth = 0;
        int overRightWidth = 0;
        // 计算初始点
        if (x > (width / 2) + reserveGap && x + (width / 2) + reserveGap < scWidth) {
            // 两边处于有空余的位置
            x -= (width / 2);
        } else if (x - (width / 2) - reserveGap < 0) {
            // 靠左超出屏幕 超出部分需要向右移动
            overLeftWidth = x - (width / 2) - reserveGap;
            x -= (width / 2) + overLeftWidth;
        }else if (x + (width / 2) + reserveGap > scWidth){
            // 靠右超出屏幕 超出部分需要向左移动
            overRightWidth = x + (width / 2) + reserveGap - scWidth;
            x = x - (width / 2) - overRightWidth;
        }else {
            x -= (width / 2);
        }
        linearLeftOffset = x;
        int leftOffset;
        if (overLeftWidth < 0){
            leftOffset = dip2px(context(), (width / 2f) + overLeftWidth);
            leftOffset -= arrowSize / 2;
        }else if (overRightWidth > 0){
            leftOffset = dip2px(context(), (width / 2f) + overRightWidth);
            leftOffset -= arrowSize / 2;
        }else {
            leftOffset = dip2px(context(), width / 2f - 5);
        }
        // 计算总共多少行
        int line = btnArray.length() / 4;
        if (btnArray.length() > 4){
            if (btnArray.length() % 4 > 0){
                line++;
            }
        }
        height = line * singHeight;
        boolean directionUp = y <= px2dip(context(), height) + 40;
        linearTopOffset = h;
        if (!directionUp) linearTopOffset = height;
        if (directionUp){
            // 如果箭头朝上则需要判断底部预留位置是否足够显示
            int startArrowPosition = y + 11 + linearTopOffset + 40 + px2dip(context(),line * singHeight);
            if (startArrowPosition > scHeight){
                linearTopOffset = linearTopOffset - (startArrowPosition - scHeight);
            }
        }
        if (longPressView == null) {
            longPressView = new LongPressView(moduleContext, context(), y, leftOffset, linearLeftOffset, linearTopOffset, directionUp,
                    style, btnArray, new LongPressView.UtilsClickListenerCallBack() {
                @Override
                public void callBackMessage(String type) {
                    JSONObject ret = new JSONObject();
                    try {
                        ret.put("type", type);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LongPressModule.this.moduleContext.success(ret, true);
                    closeWindow();
                }

                @Override
                public void close() {
                    closeWindow();
                }
            });
        }
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(300);
        longPressView.setAnimation(anim);
        insertViewToCurWindow(longPressView, rlp);
    }

    /**
     * 关闭工具栏弹窗
     */
    public void jsmethod_close(UZModuleContext moduleContext) {
        closeWindow();
    }

    private void closeWindow() {
        if (longPressView != null) {
            removeViewFromCurWindow(longPressView);
            longPressView = null;
        }
    }
}
