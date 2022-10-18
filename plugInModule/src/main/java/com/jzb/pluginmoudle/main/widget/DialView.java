package com.jzb.pluginmoudle.main.widget;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jzb.pluginmoudle.R;
import com.jzb.pluginmoudle.main.L;
import com.jzb.pluginmoudle.main.base.BaseRecyclerAdapter;
import com.jzb.pluginmoudle.main.utils.CallUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * create：2022/8/19 11:16
 *
 * @author ykx
 * @version 1.0
 * @Description 拨号盘
 */
public class DialView extends LinearLayout {

    private final Context mContext;
    private EditText editText;
    private StringBuffer stringBuffer;
    private View view;

    public DialView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public DialView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public DialView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }


    // 初始化布局
    private void initView() {
        // 绘制搜索框以及搜索图标
        setOrientation(VERTICAL);
        addView(addHeadView());
        addView(initDial());
        addView(initCallButton());
    }

    @SuppressLint("ClickableViewAccessibility")
    private LinearLayout addHeadView() {
        LinearLayout linearLayout = new LinearLayout(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        params.weight = 1;
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(VERTICAL);
        // 添加搜索框
        editText = new EditText(mContext);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(120, 0, 120, 0);
        editText.setLayoutParams(params);
        editText.setSingleLine();
        editText.setTextSize(28);
        editText.setGravity(Gravity.CENTER);
        editText.setBackground(null);
        editText.setOnTouchListener((v, event) -> true);
        editText.setTextColor(Color.parseColor("#2c2c2c"));
        editText.setCursorVisible(false);
        editText.requestFocus();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count == 0 && after > 0) {
                    // 显示光标
                    editText.setCursorVisible(true);
                } else if (after == 0) {
                    // 取消光标
                    editText.setCursorVisible(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                editText.setSelection(s.length());
            }
        });
        // 初始化分割线
        view = new View(mContext);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        view.setLayoutParams(params);
        view.setBackgroundColor(mContext.getResources().getColor(R.color.view_line_color));
        view.setVisibility(GONE);
        // 添加组件
        linearLayout.addView(editText);
        linearLayout.addView(view);
        return linearLayout;
    }

    private RecyclerView initDial() {
        RecyclerView recyclerView = new RecyclerView(mContext);
        recyclerView.setOverScrollMode(OVER_SCROLL_NEVER);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(40, 0, 40, 0);
        recyclerView.setLayoutParams(params);
        GridLayoutManager manager = new GridLayoutManager(mContext, 3);
        recyclerView.setLayoutManager(manager);
        DialAdapter adapter = new DialAdapter(mContext);
        recyclerView.setAdapter(adapter);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            list.add(String.valueOf(i));
        }
        adapter.setData(list);
        stringBuffer = new StringBuffer();
        adapter.setOnItemClickListener((position, data) -> {
            try {
                if (position == 11) {
                    if (stringBuffer.length() > 0) {
                        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                        if (stringBuffer.length() == 0) {
                            view.setVisibility(GONE);
                        }
                    }
                } else if (position < 9) {
                    stringBuffer.append(++position);
                } else if (position == 9) {
                    ClipboardManager clip = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    String text = clip.getText().toString();
                    Pattern pattern = Pattern.compile("[0-9]*");
                    if (pattern.matcher(text).matches()) {
                        stringBuffer.append(text);
                        editText.setText(stringBuffer);
                    }
                } else if (position == 10) {
                    stringBuffer.append("0");
                }
                if (stringBuffer.length() == 1) {
                    view.setVisibility(VISIBLE);
                }
                editText.setText(stringBuffer);
            } catch (Exception e) {
                L.e(e.getMessage());
            }
        });
        adapter.setOnItemLongClickListener((position, data) -> {
            if (position == 11) {
                if (stringBuffer.length() > 0) {
                    stringBuffer = new StringBuffer();
                    view.setVisibility(GONE);
                    editText.setText(stringBuffer);
                }
            }
        });
        return recyclerView;
    }

    private static class DialAdapter extends BaseRecyclerAdapter<String> {

        public DialAdapter(Context context) {
            super(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
            return new DialViewHolder(LayoutInflater.from(context).inflate(R.layout.dial_item, parent, false));
        }

        @Override
        public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, String data) {
            if (viewHolder instanceof DialViewHolder) {
                DialViewHolder holder = (DialViewHolder) viewHolder;
                holder.textView.setVisibility(GONE);
                holder.imageView.setVisibility(GONE);
                if (realPosition == 11) {
                    holder.imageView.setVisibility(VISIBLE);
                } else if (realPosition < 9) {
                    holder.textView.setVisibility(VISIBLE);
                    holder.textView.setTextSize(28);
                    holder.textView.setText(String.valueOf(realPosition + 1));
                    holder.textView.setBackgroundResource(R.drawable.dial_item_slt_bg);
                } else if (realPosition == 9) {
                    holder.textView.setVisibility(VISIBLE);
                    holder.textView.setTextSize(20);
                    holder.textView.setText("粘贴");
                    holder.textView.setBackground(null);
                } else if (realPosition == 10) {
                    holder.textView.setVisibility(VISIBLE);
                    holder.textView.setTextSize(28);
                    holder.textView.setText("0");
                    holder.textView.setBackgroundResource(R.drawable.dial_item_slt_bg);
                }
            }
        }

        private class DialViewHolder extends BaseRecyclerAdapter<String>.Holder {
            TextView textView;
            ImageView imageView;

            public DialViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.tv_number);
                imageView = itemView.findViewById(R.id.iv_clear);
            }
        }
    }

    private View initCallButton() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.include_button_call, null);
        view.findViewById(R.id.linear_call).setOnClickListener(v -> {
            if (stringBuffer.length() > 0) {
                CallUtils.getInstance().callPhone(mContext, stringBuffer.toString(), phone -> {
                    if (callPhoneBack != null) callPhoneBack.success("call_phone;" + phone);
                });
            }
        });
        return view;
    }

    private CallPhoneBack callPhoneBack;

    public interface CallPhoneBack {
        void success(String data);
    }

    public void setCallPhoneBack(CallPhoneBack callPhoneBack) {
        this.callPhoneBack = callPhoneBack;
    }
}