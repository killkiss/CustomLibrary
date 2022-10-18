package com.jzb.jzbcallhistory.adpter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jzb.jzbcallhistory.R;
import com.jzb.jzbcallhistory.bean.CustomBean;
import com.jzb.jzbcallhistory.config.Constants;
import com.jzb.jzbcallhistory.utils.CallHistoryDataUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * create：2022/8/11 11:45
 *
 * @author ykx
 * @version 1.0
 * @Description 通话详情列表
 */
public class SearchDetailAdapter extends BaseRecyclerAdapter<CustomBean.RecordsDTO> {

    public SearchDetailAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        return new SearchDetailItemViewHolder(LayoutInflater.from(context).inflate(R.layout.search_detail_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, CustomBean.RecordsDTO data) {
        SearchDetailItemViewHolder holder = (SearchDetailItemViewHolder) viewHolder;
        String time = data.getCreateTime();
        String[] times = time.split(" ");
        StringBuilder content = new StringBuilder();
        if (times.length == 2) {
            holder.tvDuration.setVisibility(View.VISIBLE);
            if (data.getTelType().equals(CallHistoryDataUtil.CALL_AUDIO)) {
                content.append("[语音通话]  ").append(times[1]);
            } else if (data.getTelType().equals(CallHistoryDataUtil.CALL_VIDEO)) {
                content.append("[视频通话]  ").append(times[1]);
            } else if (data.getTelType().equals(CallHistoryDataUtil.CALL_PHONE)) {
                content.append("[普通电话]  ").append(times[1]);
                holder.tvDuration.setVisibility(View.INVISIBLE);
            }
            holder.tvTime.setVisibility(View.VISIBLE);
            holder.viewLine.setVisibility(View.GONE);
            if (realPosition != 0) {
                // 获取上一个item的日期
                String lastTime = getListData().get(realPosition - 1).getCreateTime();
                String[] lastTimes = lastTime.split(" ");
                if (times[0].equals(lastTimes[0])) {
                    // 相同则需要隐藏当前日期
                    holder.tvTime.setVisibility(View.GONE);
                    holder.viewLine.setVisibility(View.GONE);
                } else {
                    holder.viewLine.setVisibility(View.VISIBLE);
                }
            }
            if (realPosition == getListData().size() - 1) {
                holder.viewLine2.setVisibility(View.VISIBLE);
            } else {
                holder.viewLine2.setVisibility(View.GONE);
            }
        }
        if (data.getDuration() == 0 && !data.getTelType().equals(CallHistoryDataUtil.CALL_PHONE)) {
            holder.tvCallTime.setTextColor(context.getResources().getColor(R.color.missed_call_color));
            holder.tvDuration.setTextColor(context.getResources().getColor(R.color.missed_call_color));
            holder.ivCallType.setImageDrawable(context.getResources().getDrawable(R.drawable.call_record_arrow2));
        } else {
            holder.tvCallTime.setTextColor(Color.parseColor("#000000"));
            holder.tvDuration.setTextColor(Color.parseColor("#000000"));
            holder.ivCallType.setImageDrawable(context.getResources().getDrawable(R.drawable.call_record_arrow1));
        }
        if (!data.getCreatorId().equals(Constants.unionId)) {
            holder.ivCallType.setScaleX(1);
            holder.ivCallType.setScaleY(-1);
        } else {
            holder.ivCallType.setScaleX(-1);
            holder.ivCallType.setScaleY(1);
        }
        time = times[0];
        times = time.split("-");
        time = times[1] + "月" + times[2] + "日";
        holder.tvTime.setText(time);
        if (data.getDuration() == 0) {
            holder.tvDuration.setText("未接通");
        } else if (data.getDuration() == -1) {
            holder.tvDuration.setText("0秒");
        } else {
            int duration = data.getDuration();
            if (duration < 60) {
                holder.tvDuration.setText(duration + "秒");
            } else {
                SimpleDateFormat format = new SimpleDateFormat("H小时mm分ss秒", Locale.CHINA);
                holder.tvDuration.setText(format.format(duration * 1000));
            }
        }
        holder.tvCallTime.setText(content.toString());
    }

    private class SearchDetailItemViewHolder extends BaseRecyclerAdapter<String>.Holder {
        TextView tvTime, tvCallTime, tvDuration;
        View viewLine, viewLine2;
        ImageView ivCallType;

        public SearchDetailItemViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            viewLine = itemView.findViewById(R.id.view_line);
            viewLine2 = itemView.findViewById(R.id.view_line2);
            tvCallTime = itemView.findViewById(R.id.tv_call_time);
            tvDuration = itemView.findViewById(R.id.tv_talk_time);
            ivCallType = itemView.findViewById(R.id.iv_call_type);
        }
    }
}