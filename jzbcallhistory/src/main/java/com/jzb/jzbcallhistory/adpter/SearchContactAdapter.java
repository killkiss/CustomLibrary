package com.jzb.jzbcallhistory.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jzb.jzbcallhistory.R;
import com.jzb.jzbcallhistory.bean.ContactBean;

/**
 * create：2022/8/11 10:04
 *
 * @author ykx
 * @version 1.0
 * @Description 通话记录列表适配器
 */
public class SearchContactAdapter extends BaseRecyclerAdapter<ContactBean.ListDTO> {

    public SearchContactAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        return new SearchHistoryViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_contact_item, parent, false));
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, ContactBean.ListDTO data) {
        SearchHistoryViewHolder holder = (SearchHistoryViewHolder) viewHolder;
        // 是否显示标题
        if (realPosition == 0) {
            holder.viewHead.setVisibility(View.VISIBLE);
        } else {
            holder.viewHead.setVisibility(View.GONE);
        }
        // 是否显示分割线
        if (realPosition == getListData().size() - 1) {
            holder.viewLine.setVisibility(View.GONE);
        } else {
            holder.viewLine.setVisibility(View.VISIBLE);
        }
        // 设置图片或TextImage
        if (!TextUtils.isEmpty(data.getAvatarUrl())) {
            holder.tvImage.setVisibility(View.GONE);
            // 设置图片圆角角度
            Glide.with(context).load(data.getAvatarUrl()).into(holder.imgHead);
        } else {
            holder.tvImage.setVisibility(View.VISIBLE);
            String nickName = data.getNickname();
            if (!TextUtils.isEmpty(nickName) && nickName.length() > 2){
                nickName = nickName.substring(nickName.length() - 2);
            }
            holder.tvImage.setText(nickName);
        }
        holder.tvName.setText(data.getNickname());
        // 设置职称
//        if (data.getDepart() != null) {
//            holder.tvContent.setText(data.getDepart().getDepName());
//        } else {
//            holder.tvContent.setText("暂无职称");
//        }
        holder.tvContent.setText("暂无职称");
    }

    private class SearchHistoryViewHolder extends Holder {
        View callView;
        View detailView;
        ImageView imgHead;
        TextView tvName, tvContent, tvImage;
        View viewHead, viewLine;

        public SearchHistoryViewHolder(View itemView) {
            super(itemView);
            callView = itemView.findViewById(R.id.linear_call);
            detailView = itemView.findViewById(R.id.iv_detail);
            imgHead = itemView.findViewById(R.id.iv_img);
            tvName = itemView.findViewById(R.id.tv_name);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvImage = itemView.findViewById(R.id.tv_img);
            viewHead = itemView.findViewById(R.id.linear_head);
            viewLine = itemView.findViewById(R.id.view_line);
        }
    }
}
