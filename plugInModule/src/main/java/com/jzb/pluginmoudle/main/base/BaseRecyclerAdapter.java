package com.jzb.pluginmoudle.main.base;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_NORMAL = 1;
    protected Context context;

    private List<T> listData = new ArrayList<>();

    private View mHeaderView;

    private OnItemClickListener<T> mListener;

    private OnItemLongClickListener<T> mLongListener;

    public BaseRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> mLongListener) {
        this.mLongListener = mLongListener;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void addData(ArrayList<T> data) {
        listData.addAll(data);
        notifyDataSetChanged();
    }

    public void setData(List<T> data) {
        listData = data;
        notifyDataSetChanged();
    }

    public void setListData(List<T> listData) {
        this.listData = listData;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) return TYPE_NORMAL;
        if (position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) return new Holder(mHeaderView);
        return onCreate(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        final int pos = getRealPosition(viewHolder);
        final T data = listData.get(pos);
        onBind(viewHolder, pos, data);
        if (mListener != null) {
            viewHolder.itemView.setOnClickListener(v -> {
                mListener.onItemClick(pos, data);
            });
        }
        if (mLongListener != null) {
            viewHolder.itemView.setOnLongClickListener(v -> {
                if (mLongListener != null) {
                    mLongListener.onLongClick(pos, data);
                }
                return false;
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_HEADER ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp instanceof StaggeredGridLayoutManager.LayoutParams && holder.getLayoutPosition() == 0) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        if (listData == null) {
            return 0;
        }
        return mHeaderView == null ? listData.size() : listData.size() + 1;
    }

    public abstract RecyclerView.ViewHolder onCreate(ViewGroup parent, final int viewType);

    public abstract void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, T data);

    public class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener<T> {
        void onItemClick(int position, T data);
    }

    public interface OnItemLongClickListener<T> {
        void onLongClick(int position, T data);
    }

    public List<T> getListData() {
        return listData;
    }

}