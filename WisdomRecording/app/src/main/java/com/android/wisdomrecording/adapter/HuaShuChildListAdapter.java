package com.android.wisdomrecording.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.wisdomrecording.R;
import com.android.wisdomrecording.bean.HuaShuChildInfo;
import com.android.wisdomrecording.bean.HuaShuInfo;
import com.android.wisdomrecording.interface_.CommCallBack;

import java.util.List;

/**
 * Created by zt on 2018/6/13
 */

public class HuaShuChildListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<HuaShuChildInfo> list;
    CommCallBack callBack;

    public HuaShuChildListAdapter(Context context, CommCallBack callBack) {
        this.mContext = context;
        this.callBack = callBack;
    }

    public void setData(List<HuaShuChildInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder holder = null;
        ViewGroup view = (ViewGroup) mInflater.inflate(R.layout.item_huashuliast, null, false);
        holder = new ContentViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final HuaShuChildInfo info = list.get(position);
        ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
        contentViewHolder.tv_name.setText(info.名称);
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
}
