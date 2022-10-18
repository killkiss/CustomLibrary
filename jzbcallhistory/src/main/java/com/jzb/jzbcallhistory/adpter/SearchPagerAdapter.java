package com.jzb.jzbcallhistory.adpter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jzb.jzbcallhistory.R;
import com.jzb.jzbcallhistory.bean.CustomBean;
import com.jzb.jzbcallhistory.utils.DeletePopupWindowUtil;

import java.util.List;

/**
 * create：2022/8/11 11:45
 *
 * @author ykx
 * @version 1.0
 * @Description *
 */
public class SearchPagerAdapter extends BaseRecyclerAdapter<String> implements SearchHistoryAdapter.OnDeleteClickListening, DeletePopupWindowUtil.DeleteCallBackListener {

    private final int backColor;// 背景色
    private SearchHistoryAdapter adapterAll, adapterMissed;

    public SearchPagerAdapter(Context context) {
        super(context);
        backColor = Color.parseColor("#F2F1F6");
        adapterAll = new SearchHistoryAdapter(context);
        adapterAll.setOnDeleteClickListening(this);
        adapterMissed = new SearchHistoryAdapter(context);
        adapterMissed.setOnDeleteClickListening(this);
    }

    @Override
    public void deleteDialog(String telID) {
        DeletePopupWindowUtil.getInstance().showDeleteWindow((Activity) context, adapterAll, adapterMissed, telID,this);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void DeleteSuccess() {
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        return new SearchPagerViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_history_pager, parent, false));
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, String data) {
        SearchPagerViewHolder holder = (SearchPagerViewHolder) viewHolder;
        if (data.equals("all")) {
            holder.recyclerView.setBackgroundColor(backColor);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
            holder.recyclerView.setLayoutManager(manager);
            holder.recyclerView.setAdapter(adapterAll);
            if (adapterAll.getListData().size() == 0) {
                holder.noHistoryView.setVisibility(View.VISIBLE);
            } else {
                holder.noHistoryView.setVisibility(View.GONE);
            }
        } else {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            holder.itemView.setBackgroundResource(outValue.resourceId);
            holder.recyclerView.setBackgroundColor(backColor);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
            holder.recyclerView.setLayoutManager(manager);
            holder.recyclerView.setAdapter(adapterMissed);
            if (adapterMissed.getListData().size() == 0) {
                holder.noHistoryView.setVisibility(View.VISIBLE);
            } else {
                holder.noHistoryView.setVisibility(View.GONE);
            }
        }
    }

    private class SearchPagerViewHolder extends BaseRecyclerAdapter<String>.Holder {
        RecyclerView recyclerView;
        View noHistoryView;

        public SearchPagerViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recycler_view);
            noHistoryView = itemView.findViewById(R.id.no_history_view);
        }
    }

    public void setListAll(List<CustomBean.RecordsDTO> listAll) {
        if (adapterAll != null) {
            adapterAll.setData(listAll);
        }
    }

    public void setListMissed(List<CustomBean.RecordsDTO> listMissed) {
        if (adapterMissed != null) {
            adapterMissed.setData(listMissed);
        }
    }
}
