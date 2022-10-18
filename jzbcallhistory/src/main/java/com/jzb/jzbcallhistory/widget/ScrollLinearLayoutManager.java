package com.jzb.jzbcallhistory.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * create：2022/8/11 15:52
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class ScrollLinearLayoutManager extends LinearLayoutManager {

    public boolean allowScrollHorizontally = true;
    private int startPosition = 0;

    public ScrollLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollHorizontally() {
        if (allowScrollHorizontally) {
            return false;
        } else {
            int first = findFirstCompletelyVisibleItemPosition();
            if (startPosition == first) {
                allowScrollHorizontally = true;
            }
            return super.canScrollHorizontally();
        }
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        allowScrollHorizontally = false;
        startPosition = position;
        super.smoothScrollToPosition(recyclerView, state, position);
    }

    public boolean isAllowScrollHorizontally() {
        return allowScrollHorizontally;
    }
}