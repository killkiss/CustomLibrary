package com.jzb.jzbcallhistory.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jzb.jzbcallhistory.R;

/**
 * create：2022/8/11 10:04
 *
 * @author ykx
 * @version 1.0
 * @Description tab列表适配器
 */
public class TabLayoutAdapter extends BaseRecyclerAdapter<String> {

    public TabLayoutAdapter(Context context) {
        super(context);
    }

    private int checkedPosition = 0;

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tab, parent, false);
        if (maxWidth != 0) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(maxWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(params);
        }
        return new TabLayoutViewHolder(view);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, String data) {
        TabLayoutViewHolder holder = (TabLayoutViewHolder) viewHolder;
        holder.tabName.setText(data);
        if (realPosition == checkedPosition) {
            holder.viewLine.setVisibility(View.VISIBLE);
        } else {
            holder.viewLine.setVisibility(View.INVISIBLE);
        }
    }

    public void setCheckedPosition(int checkedPosition) {
        this.checkedPosition = checkedPosition;
        notifyDataSetChanged();
    }

    private class TabLayoutViewHolder extends Holder {
        TextView tabName;
        View viewLine;

        public TabLayoutViewHolder(View itemView) {
            super(itemView);
            tabName = itemView.findViewById(R.id.tv_tab_name);
            viewLine = itemView.findViewById(R.id.view_line);
        }
    }

    private int maxWidth = 0;

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }
}
