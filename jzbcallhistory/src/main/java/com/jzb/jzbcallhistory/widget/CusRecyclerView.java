package com.jzb.jzbcallhistory.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * create：2022/8/18 16:00
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class CusRecyclerView extends RecyclerView {

    private ScrollLinearLayoutManager manager;

    public CusRecyclerView(Context context) {
        super(context);
    }

    public CusRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CusRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (manager == null) {
            manager = (ScrollLinearLayoutManager) getLayoutManager();
        }
        if (manager != null) return !manager.isAllowScrollHorizontally();
        return super.onInterceptTouchEvent(e);
    }

}
