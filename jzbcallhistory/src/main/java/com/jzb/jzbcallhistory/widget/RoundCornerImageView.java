package com.jzb.jzbcallhistory.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

/**
 * create：2022/8/17 15:54
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class RoundCornerImageView extends ImageView {
    private float width, height;
    private int leftTopRadius = 8;
    private int rightTopRadius = 8;
    private int rightBottomRadius = 8;
    private int leftBottomRadius = 8;
    private Path path;

    public RoundCornerImageView(Context context) {
        this(context, null);
        init(context, null);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context, attrs);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        leftTopRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                8, context.getResources().getDisplayMetrics());
        rightTopRadius = leftTopRadius;
        rightBottomRadius = leftTopRadius;
        leftBottomRadius = leftTopRadius;
        path = new Path();
        setScaleType(ScaleType.FIT_XY);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //这里做下判断，只有图片的宽高大于设置的圆角距离的时候才进行裁剪
        int maxLeft = Math.max(leftTopRadius, leftBottomRadius);
        int maxRight = Math.max(rightTopRadius, rightBottomRadius);
        int minWidth = maxLeft + maxRight;
        int maxTop = Math.max(leftTopRadius, rightTopRadius);
        int maxBottom = Math.max(leftBottomRadius, rightBottomRadius);
        int minHeight = maxTop + maxBottom;
        if (width >= minWidth && height > minHeight) {
            //四个角：右上，右下，左下，左上
            path.moveTo(leftTopRadius, 0);
            path.lineTo(width - rightTopRadius, 0);
            path.quadTo(width, 0, width, rightTopRadius);

            path.lineTo(width, height - rightBottomRadius);
            path.quadTo(width, height, width - rightBottomRadius, height);

            path.lineTo(leftBottomRadius, height);
            path.quadTo(0, height, 0, height - leftBottomRadius);

            path.lineTo(0, leftTopRadius);
            path.quadTo(0, 0, leftTopRadius, 0);

            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }
}
