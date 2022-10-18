package com.jzb.jzbcallhistory.utils;

import android.text.TextUtils;

import com.jzb.jzbcallhistory.adpter.SearchPagerAdapter;
import com.jzb.jzbcallhistory.bean.CustomBean;
import com.jzb.jzbcallhistory.config.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * create：2022/8/20 10:32
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class CallHistoryDataUtil {

    private static CallHistoryDataUtil instance;
    // 语音通话
    public static String CALL_AUDIO = "VOICE";
    // 视频通话
    public static String CALL_VIDEO = "VIDEO";
    // 普通电话
    public static String CALL_PHONE = "ORDINARY_CALL";
    private List<CustomBean.RecordsDTO> historyCallList;
    private SearchPagerAdapter adapter;
    private CustomBean.RecordsDTO newBean;

    public static CallHistoryDataUtil getInstance() {
        if (instance == null) {
            synchronized (CallHistoryDataUtil.class) {
                if (instance == null) {
                    instance = new CallHistoryDataUtil();
                }
            }
        }
        return instance;
    }

    public void setNewBean(CustomBean.RecordsDTO newBean) {
        this.newBean = newBean;
    }

    /**
     * 添加新通话记录
     */
    public void addNewCall(String telID,int duration) {
        if (newBean == null) {
            return;
        }
        if (!TextUtils.isEmpty(telID)){
            newBean.setTelId(telID);
        }
        // 判断是否已经存在
        for (int i = 0; i < historyCallList.size(); i++) {
            if (historyCallList.get(i).getToId().equals(newBean.getToId())) {
                // 已存在则更新时间
                String newTime = newBean.getCreateTime();
                historyCallList.get(i).setCreateTime(newTime);
                String telType = newBean.getTelType();
                newBean = historyCallList.get(i);
                newBean.setTelType(telType);
                newBean.setDuration(duration);
                historyCallList.remove(i);
                break;
            }
        }
        List<CustomBean.RecordsDTO> newList = new ArrayList<>();
        newList.add(newBean);
        if (historyCallList != null && historyCallList.size() > 0) {
            newList.addAll(historyCallList);
            historyCallList = newList;
        }
        handlerData();
    }

    public void setHistoryCallList(List<CustomBean.RecordsDTO> historyCallList, SearchPagerAdapter adapter) {
        this.historyCallList = historyCallList;
        this.adapter = adapter;
        handlerData();
    }

    /**
     * 处理数据
     */
    private void handlerData() {
        if (adapter == null || historyCallList == null) return;
        for (int i = 0; i < historyCallList.size(); i++) {
            if (!historyCallList.get(i).getCreatorId().equals(Constants.unionId)) {
                String creatorId = historyCallList.get(i).getCreatorId();
                String startName = historyCallList.get(i).getStartName();
                String startUrl = historyCallList.get(i).getStartUrl();
                historyCallList.get(i).setCreatorId(historyCallList.get(i).getToId());
                historyCallList.get(i).setStartName(historyCallList.get(i).getReceiveName());
                historyCallList.get(i).setStartUrl(historyCallList.get(i).getReceiveUrl());
                historyCallList.get(i).setToId(creatorId);
                historyCallList.get(i).setReceiveName(startName);
                historyCallList.get(i).setReceiveUrl(startUrl);
                historyCallList.get(i).setCreate(false);
            }
        }
        adapter.setListAll(historyCallList);
        // 拆分出未接听
        List<CustomBean.RecordsDTO> missedList = new ArrayList<>();
        for (int i = 0; i < historyCallList.size(); i++) {
            if (historyCallList.get(i).getDuration() == 0 && !historyCallList.get(i).getTelType().equals(CALL_PHONE)) {
                missedList.add(historyCallList.get(i));
            }
        }
        adapter.setListMissed(missedList);
        adapter.setData(Arrays.asList("all", "unconnected"));
    }
}
