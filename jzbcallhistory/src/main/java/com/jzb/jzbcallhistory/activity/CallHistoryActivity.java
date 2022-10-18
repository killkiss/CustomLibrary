package com.jzb.jzbcallhistory.activity;

import static com.jzb.jzbcallhistory.config.Constants.MODULE_TEST_VERSION;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jzb.jzbcallhistory.R;
import com.jzb.jzbcallhistory.config.BaseActivity;
import com.jzb.jzbcallhistory.config.Constants;
import com.jzb.jzbcallhistory.config.L;
import com.jzb.jzbcallhistory.config.SendBroadcastUtil;
import com.jzb.jzbcallhistory.utils.CallHistoryDataUtil;
import com.jzb.jzbcallhistory.utils.DetailPopupWindowUtil;
import com.jzb.jzbcallhistory.utils.DialPopupWindowUtil;
import com.jzb.jzbcallhistory.utils.SearchPopupWindowUtil;
import com.jzb.jzbcallhistory.widget.SearchHistoryView;


/**
 * create：2022/8/9 14:29
 *
 * @author ykx
 * @version 1.0
 * @Description 通话记录
 */
public class CallHistoryActivity extends BaseActivity implements View.OnClickListener {

    private Activity context;
    private TextView tvCall, tvDial;
    private ImageView ivCall, ivDial;
    private static int nowPosition = 0;
    private SearchHistoryView view;
    public static boolean isDetailPager = false;
    public static String callPhoneNumber;
    private JSONObject object;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Activity context, Bundle bundle) {
        super.onCreate(context, bundle);
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.context = context;
        context.setContentView(R.layout.activity_call_history);
        // 设置沉浸式
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
        Constants.unionId = bundle.getString("unionId");
        initView(bundle);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.linear_call) {
            DialPopupWindowUtil.getInstance().disWindow();
            checkChange(0);
        } else if (id == R.id.linear_dial) {
            DialPopupWindowUtil.getInstance().showSearchWindow(context);
            checkChange(1);
        }
    }

    private void initView(Bundle bundle) {
        context.findViewById(R.id.iv_back).setOnClickListener(view -> context.finish());
        // 通话历史记录页面和拨号盘的切换监听
        context.findViewById(R.id.linear_call).setOnClickListener(this);
        context.findViewById(R.id.linear_dial).setOnClickListener(this);
        tvCall = context.findViewById(R.id.tv_call);
        tvDial = context.findViewById(R.id.tv_dial);
        ivCall = context.findViewById(R.id.iv_call);
        ivDial = context.findViewById(R.id.iv_dial);
        view = context.findViewById(R.id.search_view);
        String ext = bundle.getString("ext");
        object = new JSONObject();
        object.put("fetchFields", null);
        object.put("page", 1);
        object.put("size", 100);
        object.put("count", true);
        object.put("unionId", bundle.getString("unionId"));
        object.put("token", bundle.getString("token"));
        if (TextUtils.isEmpty(ext) || ext.equalsIgnoreCase("Null")) {
            SendBroadcastUtil.sendBroadcast(context, object.toJSONString(), "IM_TEL_RECORD_LIST");
        } else {
            context.findViewById(R.id.rl_loading).setVisibility(View.GONE);
            view.setData(ext);
        }
    }

    private void checkChange(int position) {
        if (position != nowPosition) {
            nowPosition = position;
            if (position == 0) {
                tvCall.setTextColor(context.getResources().getColor(R.color.status_color));
                tvDial.setTextColor(context.getResources().getColor(R.color.normal));
                ivCall.setImageResource(R.mipmap.call_blue);
                ivDial.setImageResource(R.mipmap.dial);
            } else if (position == 1) {
                tvCall.setTextColor(context.getResources().getColor(R.color.normal));
                tvDial.setTextColor(context.getResources().getColor(R.color.status_color));
                ivCall.setImageResource(R.mipmap.call);
                ivDial.setImageResource(R.mipmap.dial_blue);
            }
        }
    }

    @Override
    public void hostCallBack(Activity context, String requestAPI, String data) {
        super.hostCallBack(context, requestAPI, data);
        JSONObject object;
        switch (requestAPI) {
            case "GET_USER_LIST_BY_DEPTID":
                SearchPopupWindowUtil.getInstance().setData(data);
                break;
            case "IM_TEL_RECORD_CREATE":
                object = JSON.parseObject(data);
                if (object.getString("code").equals("30000")) {
                    CallHistoryDataUtil.getInstance().addNewCall(object.getString("telId"),object.getInteger("duration"));
                }
                break;
            case "IM_TEL_RECORD_DETAIL":
                DetailPopupWindowUtil.getInstance().setData(data);
                break;
            case "IM_TEL_RECORD_REFRESH":
            case "IM_TEL_RECORD_LIST":
                try {
                    if (view == null) {
                        view = context.findViewById(R.id.search_view);
                    }
                    context.findViewById(R.id.rl_loading).setVisibility(View.GONE);
                    view.setData(data);
                } catch (Exception e) {
                    L.e(e.getMessage());
                }
                break;
            case "CALL_AUDIO":
            case "CALL_VIDEO":
                if (isDetailPager) {
                    DetailPopupWindowUtil.getInstance().refreshData();
                }
                // 添加新数据
                object = JSON.parseObject(data);
                if (object.getString("code").equals("30000")) {
                    CallHistoryDataUtil.getInstance().addNewCall(object.getString("telId"),object.getInteger("duration"));
                }
                break;
            case "USER_ACCOUNT_GET_ACCOUNT_INFO":
                // 获取手机号码
                object = JSON.parseObject(data);
                if (object.getString("code").equals("30000")) {
                    CallHistoryActivity.callPhoneNumber = object.getJSONObject("model").getString("phone");
                }
                break;
        }
    }
}