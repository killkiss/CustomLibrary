package com.jzb.jzbcallhistory.adpter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jzb.jzbcallhistory.R;
import com.jzb.jzbcallhistory.activity.CallHistoryActivity;
import com.jzb.jzbcallhistory.bean.CustomBean;
import com.jzb.jzbcallhistory.config.L;
import com.jzb.jzbcallhistory.utils.CallHistoryDataUtil;
import com.jzb.jzbcallhistory.utils.CallPopupWindowUtil;
import com.jzb.jzbcallhistory.utils.DetailPopupWindowUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * create：2022/8/11 10:04
 *
 * @author ykx
 * @version 1.0
 * @Description 通话记录列表适配器
 */
public class SearchHistoryAdapter extends BaseRecyclerAdapter<CustomBean.RecordsDTO> {

    public SearchHistoryAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        return new SearchHistoryViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_call_history_item, parent, false));
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, CustomBean.RecordsDTO data) {
        SearchHistoryViewHolder holder = (SearchHistoryViewHolder) viewHolder;
        String headUrl;
        String tvName = data.getReceiveName();
        if (data.getUnReceivedNum() > 1) {
            tvName = tvName + "(" + data.getUnReceivedNum() + ")";
        }
        holder.tvName.setText(tvName);
        headUrl = data.getReceiveUrl();
        String time = data.getCreateTime();
        if (!time.equals("刚刚")) {
            String[] times = time.split(" ");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            String formatTime = format.format(System.currentTimeMillis());
            if (times.length == 2) {
                try {
                    if (formatTime.equals(times[0])) {
                        // 当天需要区分上午下午
                        String[] detailTime = times[1].split(":");
                        if (detailTime.length == 3) {
                            if (Integer.parseInt(detailTime[0]) < 12) {
                                time = "上午" + Integer.parseInt(detailTime[0]) + ":" + detailTime[1];
                            } else {
                                time = "下午" + Integer.parseInt(detailTime[0]) + ":" + detailTime[1];
                            }
                        }
                    } else {
                        time = times[0];
                        times = time.split("-");
                        time = times[1] + "月" + times[2] + "日";
                    }
                } catch (Exception e) {
                    L.e(e.getMessage());
                }
            } else {
                time = data.getCreateTime();
            }
        }
        StringBuffer content = new StringBuffer();
        if (data.getTelType().equals(CallHistoryDataUtil.CALL_AUDIO)) {
            content.append("[语音]  ").append(time);
        } else if (data.getTelType().equals(CallHistoryDataUtil.CALL_VIDEO)) {
            content.append("[视频]  ").append(time);
        } else if (data.getTelType().equals(CallHistoryDataUtil.CALL_PHONE)) {
            content.append("[普通电话]  ").append(time);
        }
        holder.tvContent.setText(content);
        if (!data.isCreate()) {
            holder.ivRightArrow.setVisibility(View.GONE);
        } else {
            holder.ivRightArrow.setVisibility(View.VISIBLE);
        }
        if (data.getDuration() == 0 && !data.getTelType().equals(CallHistoryDataUtil.CALL_PHONE)) {
            holder.tvContent.setTextColor(context.getResources().getColor(R.color.missed_call_color));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.missed_call_color));
            holder.ivRightArrow.setImageDrawable(context.getResources().getDrawable(R.drawable.call_record_arrow2));
        } else {
            holder.tvContent.setTextColor(context.getResources().getColor(R.color.content_color));
            holder.tvName.setTextColor(Color.parseColor("#333333"));
            holder.ivRightArrow.setImageDrawable(context.getResources().getDrawable(R.drawable.call_record_arrow_gray));
        }
        // 设置图片或TextImage
        if (!TextUtils.isEmpty(headUrl)) {
            holder.tvHead.setVisibility(View.GONE);
            // 设置图片圆角角度
            Glide.with(context).load(headUrl).into(holder.imgHead);
        } else {
            holder.tvHead.setVisibility(View.VISIBLE);
            String imgName = data.getReceiveName();
            if (!TextUtils.isEmpty(imgName) && imgName.length() > 2) {
                imgName = imgName.substring(imgName.length() - 2);
            }
            holder.tvHead.setText(imgName);
        }
        holder.callView.setOnLongClickListener(v -> {
            if (onDeleteClickListening != null) {
                onDeleteClickListening.deleteDialog(data.getTelId());
            }
            return false;
        });
        holder.callView.setOnClickListener(v -> {
            CallHistoryActivity.isDetailPager = false;
            CallHistoryActivity.callPhoneNumber = null;
            CallPopupWindowUtil.getInstance().showSearchWindow((Activity) context, data.getReceiveName(),
                    data.getReceiveUrl(), data.getCreatorId(), data.getToId());
        });
        holder.detailView.setOnClickListener(v -> {
            if (data.isCreate()) {
                DetailPopupWindowUtil.getInstance().showSearchWindow((Activity) context,
                        data.getReceiveUrl(), data.getReceiveName(), data.getCreatorId(), data.getToId());
            } else {
                DetailPopupWindowUtil.getInstance().showSearchWindow((Activity) context,
                        data.getReceiveUrl(), data.getReceiveName(), data.getToId(), data.getCreatorId());
            }
        });
    }

    private class SearchHistoryViewHolder extends BaseRecyclerAdapter<CustomBean>.Holder {
        View callView;
        View detailView;
        TextView tvName, tvContent, tvHead;
        ImageView imgHead, ivLeftArrow, ivRightArrow;

        public SearchHistoryViewHolder(View itemView) {
            super(itemView);
            callView = itemView.findViewById(R.id.linear_call);
            detailView = itemView.findViewById(R.id.iv_detail);
            tvName = itemView.findViewById(R.id.tv_name);
            tvContent = itemView.findViewById(R.id.tv_content);
            imgHead = itemView.findViewById(R.id.iv_img);
            tvHead = itemView.findViewById(R.id.tv_img);
            ivLeftArrow = itemView.findViewById(R.id.iv_left_arrow);
            ivRightArrow = itemView.findViewById(R.id.iv_right_arrow);
        }
    }

    private OnDeleteClickListening onDeleteClickListening;

    public interface OnDeleteClickListening {
        void deleteDialog(String telID);
    }

    public void setOnDeleteClickListening(OnDeleteClickListening onDeleteClickListening) {
        this.onDeleteClickListening = onDeleteClickListening;
    }
}