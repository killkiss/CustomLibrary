package com.jzb.jzbcallhistory.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jzb.jzbcallhistory.R;
import com.jzb.jzbcallhistory.adpter.SearchPagerAdapter;
import com.jzb.jzbcallhistory.bean.CustomBean;
import com.jzb.jzbcallhistory.utils.CallHistoryDataUtil;
import com.jzb.jzbcallhistory.utils.SearchPopupWindowUtil;

/**
 * create：2022/8/10 17:24
 *
 * @author ykx
 * @version 1.0
 * @Description 通话记录搜索控件
 */
public class SearchHistoryView extends LinearLayout {

    private final Context mContext;
    private SearchPagerAdapter adapter;
    private CusRecyclerView recyclerView;

    public SearchHistoryView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public SearchHistoryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public SearchHistoryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    // 初始化布局
    private void initView() {
        // 绘制搜索框以及搜索图标
        setOrientation(VERTICAL);
        addView(addHeadView());
        addView(initTab());
        addView(initLine());
        addView(initBody());
    }

    /**
     * 添加切换控件
     */
    private CusTabLayout initTab() {
        CusTabLayout tableLayout = new CusTabLayout(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tableLayout.setLayoutParams(params);
        tableLayout.setOnItemClickListener((position, data) -> {
            if (recyclerView != null) {
                tableLayout.setTabPosition(position);
                recyclerView.smoothScrollToPosition(position);
            }
        });
        tableLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                String[] tabs = new String[]{"所有通话", "未接电话"};
                tableLayout.setTab(tabs, tableLayout.getWidth() / tabs.length);
                tableLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        return tableLayout;
    }

    private RelativeLayout addHeadView() {
        RelativeLayout headView = new RelativeLayout(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, dip2px(35));
        params.setMargins(dip2px(10), dip2px(10), dip2px(10), dip2px(5));
        headView.setLayoutParams(params);
        headView.setBackgroundResource(R.drawable.search_edittext_radius);
        // 添加搜索框
        TextView editText = new TextView(mContext);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(params);
        editText.setHint("搜索");
        editText.setSingleLine();
        editText.setTextSize(16);
        editText.setGravity(Gravity.CENTER_VERTICAL);
        editText.setPadding(dip2px(35), 0, 0, 0);
        editText.setHintTextColor(Color.parseColor("#9C9CA1"));
        editText.setBackground(null);
        editText.setTextColor(Color.parseColor("#000000"));
        editText.setCursorVisible(true);
        editText.setOnClickListener(v -> SearchPopupWindowUtil.getInstance().showSearchWindow((Activity) mContext));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            editText.setTextCursorDrawable(R.drawable.edittext_cursor);
        }
        // 添加搜索图标
        ImageView imageView = new ImageView(mContext);
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(dip2px(13), dip2px(13));
        imageParams.addRule(RelativeLayout.CENTER_VERTICAL);
        imageParams.leftMargin = dip2px(12);
        imageView.setLayoutParams(imageParams);
        imageView.setImageResource(R.mipmap.history_search);
        // 添加到组件
        headView.addView(editText);
        headView.addView(imageView);
        return headView;
    }

    /**
     * 添加搜索结果组件
     */
    private RecyclerView initBody() {
        recyclerView = new CusRecyclerView(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutParams(params);
        recyclerView.setBackgroundColor(Color.parseColor("#F2F1F6"));
        ScrollLinearLayoutManager manager = new ScrollLinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        adapter = new SearchPagerAdapter(mContext);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 创建分割线
     */
    private View initLine() {
        View view = new View(mContext);
        ViewGroup.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        view.setLayoutParams(params);
        view.setBackgroundColor(Color.parseColor("#dbdbdb"));
        return view;
    }

    public void setData(String data) {
        // 录入全部数据
        if (adapter == null || TextUtils.isEmpty(data)) return;
        CustomBean bean = JSONObject.parseObject(data, CustomBean.class);
        CallHistoryDataUtil.getInstance().setHistoryCallList(bean.getRecords(), adapter);
    }

}