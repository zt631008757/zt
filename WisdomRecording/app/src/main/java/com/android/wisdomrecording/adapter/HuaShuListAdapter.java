package com.android.wisdomrecording.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.wisdomrecording.R;
import com.android.wisdomrecording.bean.HuaShuInfo;
import com.android.wisdomrecording.bean.TaskInfo;
import com.android.wisdomrecording.interface_.CommCallBack;

import java.util.List;

/**
 * Created by zt on 2018/6/13
 */

public class HuaShuListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<HuaShuInfo> list;
    CommCallBack callBack;

    final int TYPE_TITLE = 1;
    final int TYPE_CONTENT = 2;

    public HuaShuListAdapter(Context context, CommCallBack callBack) {
        this.mContext = context;
        this.callBack = callBack;
    }

    public void setData(List<HuaShuInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder holder = null;
        if (viewType == TYPE_TITLE) {
            ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.item_huashulist_title, null, false);
            holder = new TitleViewHolder(view);
        } else {
            ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.item_huashuliast, null, false);
            holder = new ContentViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final HuaShuInfo info = list.get(position);
        if (getItemViewType(position) == TYPE_TITLE) {
            TitleViewHolder viewHolder = (TitleViewHolder) holder;
            viewHolder.tv_title.setText(info.titleName);
        } else {
            ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
            if (!TextUtils.isEmpty(info.话术名称)) {
                contentViewHolder.tv_name.setText(info.话术名称);
            } else if (!TextUtils.isEmpty(info.知识库名称)) {
                contentViewHolder.tv_name.setText(info.知识库名称);
            }
            if("待录音".equals(info.状态))
            {
                contentViewHolder.tv_statu.setTextColor(mContext.getResources().getColor(R.color.red));
            }
            else
            {
                contentViewHolder.tv_statu.setTextColor(mContext.getResources().getColor(R.color.green));
            }
            contentViewHolder.tv_statu.setText(info.状态);
            contentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callBack != null) {
                        callBack.onResult(info);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        HuaShuInfo info = list.get(position);
        if (info.isTitle) {
            return TYPE_TITLE;
        } else {
            return TYPE_CONTENT;
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_statu;

        public ContentViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_statu = (TextView) itemView.findViewById(R.id.tv_statu);
        }
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;

        public TitleViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
