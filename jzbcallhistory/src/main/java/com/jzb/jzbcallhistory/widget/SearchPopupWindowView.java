package com.jzb.jzbcallhistory.widget;

import static com.jzb.jzbcallhistory.utils.CallUtils.dp2px;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jzb.jzbcallhistory.R;
import com.jzb.jzbcallhistory.adpter.SearchContactAdapter;
import com.jzb.jzbcallhistory.bean.ContactBean;
import com.jzb.jzbcallhistory.config.Constants;
import com.jzb.jzbcallhistory.config.L;
import com.jzb.jzbcallhistory.config.SendBroadcastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * create：2022/8/10 17:24
 *
 * @author ykx
 * @version 1.0
 * @Description 通话记录搜索控件
 */
public class SearchPopupWindowView extends LinearLayout {

    private final Activity mContext;
    private EditText editText;
    private ImageView imageBack;
    private TextView textView;
    private SearchContactAdapter adapter;
    private ImageView imageClear;
    private LinearLayout linearView;

    public SearchPopupWindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = (Activity) context;
        initView();
    }

    // 初始化布局
    private void initView() {
        // 绘制搜索框以及搜索图标
        setOrientation(VERTICAL);
        addView(addHeadView());
        addView(addLoading());
        addView(initLine());
        addView(initHintView());
        addView(initBody());
    }

    private LinearLayout addHeadView() {
        LinearLayout view = new LinearLayout(mContext);
        view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.setOrientation(HORIZONTAL);
        view.setPadding(0, dip2px(35), 0, 0);
        view.setBackgroundColor(mContext.getResources().getColor(R.color.status_color));
        // 添加返回键
        imageBack = new ImageView(mContext);
        imageBack.setLayoutParams(new LayoutParams(dip2px(45), dip2px(45)));
        imageBack.setImageResource(R.mipmap.ic_back_white);
        int padding = dip2px(13);
        imageBack.setPadding(padding, padding, padding, padding);
        view.addView(imageBack);
        // 添加搜索组件
        RelativeLayout headView = new RelativeLayout(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, dip2px(35));
        params.setMargins(0, dip2px(5), dip2px(10), dip2px(5));
        headView.setLayoutParams(params);
        headView.setBackgroundResource(R.drawable.search_edittext_radius);
        // 添加搜索框
        editText = new EditText(mContext);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(params);
        editText.setHint("搜索");
        editText.setSingleLine();
        editText.setTextSize(16);
        editText.setPadding(dip2px(35), 0, 0, 0);
        editText.setHintTextColor(Color.parseColor("#9C9CA1"));
        editText.setBackground(null);
        editText.setTextColor(Color.parseColor("#000000"));
        editText.setCursorVisible(true);
        editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchDelayHandler != null) {
                    searchDelayHandler.removeCallbacksAndMessages(null);
                }
                if (s.toString().length() > 0) {
                    if (imageClear != null) {
                        imageClear.setVisibility(VISIBLE);
                    }
                    searchDelayHandler = new SearchDelayHandler();
                    searchDelayHandler.sendEmptyMessageDelayed(1, 1000);
                } else {
                    if (imageClear != null) {
                        imageClear.setVisibility(GONE);
                    }
                }
            }
        });
        editText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                if (searchDelayHandler != null) searchDelayHandler.removeCallbacksAndMessages(null);
                String keywords = editText.getText().toString();
                if (!TextUtils.isEmpty(keywords)) {
                    searchDelayHandler = new SearchDelayHandler();
                    searchDelayHandler.sendEmptyMessage(0);
                    return false;
                }
                return true;
            }
            return false;
        });
        editText.requestFocus();
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
        // 添加删除按钮
        imageClear = new ImageView(mContext);
        imageParams = new RelativeLayout.LayoutParams(dip2px(23), dip2px(23));
        imageParams.addRule(RelativeLayout.CENTER_VERTICAL);
        imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageParams.rightMargin = dip2px(8);
        imageClear.setLayoutParams(imageParams);
        padding = dip2px(4);
        imageClear.setPadding(padding, padding, padding, padding);
        imageClear.setImageResource(R.mipmap.search_clear);
        imageClear.setVisibility(GONE);
        imageClear.setOnClickListener(v -> {
            if (searchDelayHandler != null) searchDelayHandler.removeCallbacksAndMessages(null);
            editText.setText("");
        });
        // 添加到组件
        headView.addView(editText);
        headView.addView(imageView);
        headView.addView(imageClear);
        view.addView(headView);
        return view;
    }

    private View addLoading() {
        linearView = new LinearLayout(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearView.setLayoutParams(params);
        linearView.setBackgroundColor(Color.parseColor("#F2F1F6"));
        linearView.setOrientation(LinearLayout.VERTICAL);
        // 添加等待效果
        LinearLayout linearLoading = new LinearLayout(mContext);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(mContext, 50));
        params.topMargin = dip2px(10);
        linearLoading.setBackgroundColor(Color.parseColor("#FFFFFF"));
        params.gravity = Gravity.CENTER;
        linearLoading.setLayoutParams(params);
        linearLoading.setGravity(Gravity.CENTER);
        // 添加等待图片
        ProgressBar progressBar = new ProgressBar(mContext);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.getIndeterminateDrawable().setTint(Color.parseColor("#1B7BD9"));
        }
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(dip2px(30), dip2px(30)));
        linearLoading.addView(progressBar);
        // 添加等待文字
        TextView textView = new TextView(mContext);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = dip2px(14);
        textView.setLayoutParams(params);
        textView.setTextSize(14);
        textView.setTextColor(mContext.getResources().getColor(R.color.content_color));
        textView.setText("正在加载");
        linearLoading.addView(textView);
        linearView.addView(linearLoading);
        linearView.setVisibility(GONE);
        return linearView;
    }

    private TextView initHintView() {
        // 添加文字描述
        textView = new TextView(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);
        textView.setBackgroundColor(Color.parseColor("#F2F1F6"));
        textView.setPadding(0, dip2px(30), 0, 0);
        textView.setText("没有找到相关结果");
        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextColor(Color.parseColor("#9C9CA1"));
        textView.setVisibility(GONE);
        return textView;
    }

    /**
     * 添加搜索结果组件
     */
    private RecyclerView initBody() {
        RecyclerView recyclerView = new RecyclerView(mContext);
        recyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutParams(params);
        recyclerView.setBackgroundColor(Color.parseColor("#F2F1F6"));
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        adapter = new SearchContactAdapter(mContext);
        adapter.setOnItemClickListener((position, data) -> finishCallBack.finished(data.getNickname(), data.getUserId(), data.getAvatarUrl(), data.getUnionid(), data.getPhone()));
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

    public EditText getEditText() {
        return editText;
    }

    public ImageView getImageBack() {
        return imageBack;
    }

    public void setAdapterData(String data) {
        // 解析数据
        ContactBean jsonObject = JSON.parseObject(data, ContactBean.class);
        if (jsonObject.getCode().equals("30000")) {
            List<ContactBean.ListDTO> list = jsonObject.getList();
            if (list.size() == 0) {
                textView.setVisibility(VISIBLE);
            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (!TextUtils.isEmpty(list.get(i).getUnionid()) && list.get(i).getUnionid().equals(Constants.unionId)) {
                        // 去除本人
                        list.remove(i);
                        break;
                    }
                }
                textView.setVisibility(GONE);
            }
            adapter.setData(list);
        }
        if (linearView != null) linearView.setVisibility(GONE);
    }

    private FinishCallBack finishCallBack;

    public interface FinishCallBack {
        void finished(String nickname, String userId, String headURL, String unionId, String phone);
    }

    public void setFinishCallBack(FinishCallBack finishCallBack) {
        this.finishCallBack = finishCallBack;
    }

    /**
     * 用户输入内容后的延迟加载
     * 用户输入内容一秒钟内如果没有输入和搜索操作则主动搜索内容
     */
    private SearchDelayHandler searchDelayHandler;

    private class SearchDelayHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String keywords = editText.getText().toString();
            adapter.setData(new ArrayList<>());
            if (linearView != null) linearView.setVisibility(VISIBLE);
            String ext = "{\"keywords\":" + "\"" + keywords + "\"}";
            SendBroadcastUtil.sendBroadcast(mContext, ext, "GET_USER_LIST_BY_DEPTID");
            if (msg.what == 0) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 释放组件资源
     */
    public void releaseView() {
        try {
            if (linearView != null) linearView.setVisibility(GONE);
            if (searchDelayHandler != null) searchDelayHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            L.e(e.getMessage());
        }
    }
}