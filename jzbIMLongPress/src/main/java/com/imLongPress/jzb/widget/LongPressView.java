package com.imLongPress.jzb.widget;

import static com.imLongPress.jzb.utls.PressDrawableHelper.dip2px;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.imLongPress.jzb.R;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * create：2022/4/13 16:10
 *
 * @author ykx
 * @version 1.0
 * @Description 长按工具栏弹窗view
 */
public class LongPressView extends LinearLayout {

    // 背景颜色
    private String bgColor = "#FF575757";
    // 选中高亮颜色
    private String lightColor = "#6A6A6A";
    // 边框颜色
    private String borderColor;
    // 分割线颜色
    private String cutLineColor;
    // 标题颜色
    private String titleColor;
    // 单个宽度
    public static int singWidth = 0;
    // 单个高度
    private UZModuleContext moduleContext;
    public static int singHeight = 0;
    // 箭头尺寸
    public static int arrowSize = 0;

    /**
     * @param moduleContext    h5对象
     * @param context          上下文
     * @param y                起始y轴
     * @param leftOffset       箭头向左偏移量
     * @param linearLeftOffset 整体向左偏移量
     * @param linearTopOffset  整体向上偏移量
     * @param directionUp      箭头是否朝上
     * @param style            消息类型 3图片 7位置
     * @param btnArray         展示按钮集合
     * @param listener         点击回调
     */
    public LongPressView(UZModuleContext moduleContext, Context context,
                         int y, int leftOffset, int linearLeftOffset, int linearTopOffset, boolean directionUp,
                         JSONObject style, JSONArray btnArray, UtilsClickListenerCallBack listener) {
        super(context);
        this.moduleContext = moduleContext;
        setOrientation(VERTICAL);
        setOnClickListener(view -> listener.close());
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = dip2px(context, linearLeftOffset);
        // 调整位置
        if (directionUp) {
            params.topMargin = dip2px(context, y + 11 + linearTopOffset);
        } else {
            params.topMargin = dip2px(context, y - 17) - linearTopOffset;
        }
        linearLayout.setOrientation(VERTICAL);
        linearLayout.setLayoutParams(params);
        addView(linearLayout);
        // 获取颜色属性
        bgColor = style.optString("bgColor");
        lightColor = style.optString("lightColor");
        borderColor = style.optString("borderColor");
        cutLineColor = style.optString("cutLineColor");
        titleColor = style.optString("titleColor");
        init(linearLayout, context, leftOffset, directionUp, btnArray, listener);
    }

    public LongPressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LongPressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 初始化配置
     */
    private void init(LinearLayout all, Context context, int leftOffset,
                      boolean directionUp, JSONArray btnArray, final UtilsClickListenerCallBack listener) {
        LinearLayout linearLayout = null;
        View view = new View(context);
        LayoutParams params;
        // 箭头向下view设定
        if (directionUp) {
            // 通过x值判断箭头靠左还是靠右还是居中情况
            params = new LayoutParams(arrowSize, arrowSize);
            params.leftMargin = leftOffset;
            params.topMargin = dip2px(context, -4);
            view.setLayoutParams(params);
            view.setBackgroundResource(R.drawable.triangle);
            all.addView(view);
        }
        // 核心内容展示
        LinearLayout linearLayoutBody = new LinearLayout(context);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayoutBody.setOrientation(VERTICAL);
        linearLayoutBody.setLayoutParams(params);
        linearLayoutBody.setBackgroundResource(R.drawable.long_press_view_shape);
        all.addView(linearLayoutBody);
        int maxLine = btnArray.length() / 4;
        if (btnArray.length() > 4){
            if (btnArray.length() % 4 > 0){
                maxLine++;
            }
        }
        Log.e("-main-","maxLine = " + maxLine);
        // 添加展示组件
        for (int i = 0, line = 0; i < btnArray.length(); i++) {
            if (i % 4 == 0) {
                line++;
                // 添加分割线
                view = new View(context);
                params = new LayoutParams(LayoutParams.MATCH_PARENT, 2);
                params.setMargins(dip2px(context, 13), 0, dip2px(context, 13), 0);
                view.setLayoutParams(params);
                view.setBackgroundColor(Color.parseColor(cutLineColor));
                linearLayoutBody.addView(view);
                // 添加下一行容器
                linearLayout = new LinearLayout(context);
                params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearLayout.setLayoutParams(params);
                linearLayout.setOrientation(HORIZONTAL);
                linearLayoutBody.addView(linearLayout);
            }
            // 获取展示类型图片和标题
            JSONObject object = btnArray.optJSONObject(i);
            final String type = object.optString("type");
            String icon = object.optString("icon");
            String title = object.optString("title");
            view = LayoutInflater.from(context).
                    inflate(R.layout.view_long_press, null, false);
            LinearLayout linearBody = view.findViewById(R.id.linear_body);
            params = (LayoutParams) linearBody.getLayoutParams();
            params.width = singWidth;
            params.height = singHeight;
            linearBody.setLayoutParams(params);
            // 根据条件设置背景
            if (line == 0 && btnArray.length() < 5) {
                // 只有一个的情况
                if (i == 0 && btnArray.length() == 1) {
                    view.setBackgroundResource(R.drawable.long_press_view_select);
                } else {
                    // 只有一行的情况
                    if (i == 0) {
                        view.setBackgroundResource(R.drawable.long_press_view_select_tbleft);
                    } else if (i == btnArray.length() - 1) {
                        view.setBackgroundResource(R.drawable.long_press_view_select_tbright);
                    } else {
                        view.setBackgroundResource(R.drawable.long_press_view_select_center);
                    }
                }
            } else {
                if (i == 0) {
                    view.setBackgroundResource(R.drawable.long_press_view_select_topleft);
                } else if (i == 3) {
                    view.setBackgroundResource(R.drawable.long_press_view_select_topright);
                } else if (maxLine == line && i == ((maxLine * 4) - 4)) {
                    view.setBackgroundResource(R.drawable.long_press_view_select_left);
                } else if (maxLine == line && i == ((maxLine * 4) - 1)) {
                    view.setBackgroundResource(R.drawable.long_press_view_select_right);
                } else {
                    view.setBackgroundResource(R.drawable.long_press_view_select_center);
                }
            }
            // 添加内容
            FontAdaptionTextView textView = view.findViewById(R.id.tv_title);
            if (!TextUtils.isEmpty(icon)) {
                textView.setPadding(0, dip2px(context, 3), 0, 0);
            }
            ((TextView) view.findViewById(R.id.tv_title)).setTextColor(Color.parseColor(titleColor));
            ((TextView) view.findViewById(R.id.tv_title)).setText(title);
            if (!TextUtils.isEmpty(icon)) {
                ImageView imageView = view.findViewById(R.id.iv_img);
                imageView.setVisibility(VISIBLE);
                setImage(icon, imageView);
            }
            view.setOnClickListener(view1 -> listener.callBackMessage(type));
            // 注入一个按钮
            linearLayout.addView(view);
        }
        // 箭头向上view设置
        if (!directionUp) {
            view = new View(context);
            params = new LayoutParams(arrowSize, arrowSize);
            params.leftMargin = leftOffset;
            params.bottomMargin = dip2px(context, -4);
            view.setLayoutParams(params);
            view.setBackgroundResource(R.drawable.inverted_triangle);
            all.addView(view);
        }
    }

    public interface UtilsClickListenerCallBack {
        void callBackMessage(String type);

        void close();
    }

    private void setImage(String icon, ImageView imageView) {
        icon = moduleContext.makeRealPath(icon);
        Bitmap bitmap = UZUtility.getLocalImage(icon);
        if (null != bitmap) {
            Resources res = getContext().getResources();
            Drawable drawable = new BitmapDrawable(res, bitmap);
            imageView.setImageDrawable(drawable);
        }
    }

}
