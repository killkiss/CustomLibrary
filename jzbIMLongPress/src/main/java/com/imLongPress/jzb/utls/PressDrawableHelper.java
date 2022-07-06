package com.imLongPress.jzb.utls;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * create：2022/4/18 17:03
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class PressDrawableHelper {

    /**
     * 创建drawable
     * @param context 上下文对象
     * @param normalColor 普通状态背景色
     * @param pressedColor 持续按下背景色
     * @param topLeftRadius 左上弧度
     * @param topRightRadius 右上弧度
     * @param bottomLeftRadius 左下弧度
     * @param bottomRightRadius 右下弧度
     * @return StateListDrawable
     */
    public static StateListDrawable createPressDrawable(Context context, String normalColor, String pressedColor,
                                                        float topLeftRadius, float topRightRadius,
                                                        float bottomLeftRadius, float bottomRightRadius) {
        StateListDrawable drawable = new StateListDrawable();
        // 按触
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor(pressedColor));
        gradientDrawable.setCornerRadii(new float[]{dip2px(context, topLeftRadius),
                dip2px(context, topRightRadius), dip2px(context, bottomLeftRadius),
                dip2px(context, bottomRightRadius)});
        drawable.addState(new int[]{android.R.attr.state_pressed}, gradientDrawable);
        // 默认
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor(normalColor));
        gradientDrawable.setCornerRadii(new float[]{dip2px(context, topLeftRadius),
                dip2px(context, topRightRadius), dip2px(context, bottomLeftRadius),
                dip2px(context, bottomRightRadius)});
        drawable.addState(new int[]{}, gradientDrawable);

        return drawable;
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
