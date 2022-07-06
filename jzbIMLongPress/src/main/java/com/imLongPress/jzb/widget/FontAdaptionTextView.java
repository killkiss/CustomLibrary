package com.imLongPress.jzb.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * create：2022/6/30 14:57
 *
 * @author ykx
 * @version 1.0
 * @Description 超出宽度自适应文字大小
 */
public class FontAdaptionTextView extends TextView {
    public FontAdaptionTextView(Context context) {
        super(context);
    }

    public FontAdaptionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FontAdaptionTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredWidth() != 0) {

            //判断View字体行数
            if (getLayout().getLineCount() > 1) {
                //获取view内部间隔
                int l = getPaddingLeft();
                int r = getPaddingRight();
                float pp = 0;
                //计算字符串长度
                for (int i = 0; i < getLayout().getLineCount(); i++) {
                    pp = pp + l + r + getLayout().getLineWidth(i);
                }

                //计算view的宽度与字符串长度的比例
                float f = getMeasuredWidth() / pp;
                //获取缩放后的字体高度
                float s = getTextSize() * f;
                //设置控件字体大小
                setTextSize(TypedValue.COMPLEX_UNIT_PX, s);
            }
        }
    }

}
