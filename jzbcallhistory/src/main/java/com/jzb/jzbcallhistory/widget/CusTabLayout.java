package com.jzb.jzbcallhistory.widget;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.jzb.jzbcallhistory.R;
import com.jzb.jzbcallhistory.adpter.BaseRecyclerAdapter;
import com.jzb.jzbcallhistory.adpter.TabLayoutAdapter;

import java.util.Arrays;

/**
 * create：2022/8/12 10:53
 *
 * @author ykx
 * @version 1.0
 * @Description 自定义TabLayout
 */
public class CusTabLayout extends LinearLayout {

    private Activity mContext;
    private TabLayoutAdapter adapter;

    public CusTabLayout(Context context) {
        super(context);
        mContext = (Activity) context;
        addView(addRecyclerView());
    }

    /**
     * 添加横向滑动的recycler
     */
    private RecyclerView addRecyclerView() {
        RecyclerView mRecyclerView = new RecyclerView(mContext);
        mRecyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mRecyclerView.setLayoutParams(params);
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(manager);
        adapter = new TabLayoutAdapter(mContext);
        mRecyclerView.setAdapter(adapter);

        return mRecyclerView;
    }

    /**
     * 设置tab
     *
     * @param strings 数据源
     */
    public void setTab(String[] strings, int maxWidth) {
        adapter.setMaxWidth(maxWidth);
        adapter.setData(Arrays.asList(strings));
    }

    /**
     * 设置点击事件
     */
    public void setOnItemClickListener(BaseRecyclerAdapter.OnItemClickListener listener) {
        if (adapter != null) adapter.setOnItemClickListener(listener);
    }

    /**
     * 设置选中的tab
     */
    public void setTabPosition(int position) {
        adapter.setCheckedPosition(position);
    }
}
