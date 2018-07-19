package com.android.wisdomrecording.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.wisdomrecording.R;
import com.android.wisdomrecording.bean.TaskInfo;
import com.android.wisdomrecording.interface_.CommCallBack;

import java.util.List;

/**
 * Created by zt on 2018/6/13
 */

public class TaskListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<TaskInfo> list;
    CommCallBack callBack;

    public TaskListAdapter(Context context, CommCallBack callBack) {
        this.mContext = context;
        this.callBack = callBack;
    }

    public void setData(List<TaskInfo> list)
    {
        this.list=list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        ViewGroup vImage = (ViewGroup) mInflater.inflate(R.layout.item_tasklist, null, false);
        ContentViewHolder vhImage = new ContentViewHolder(vImage);
        return vhImage;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
        final TaskInfo tagInfo = list.get(position);
        contentViewHolder.tv_name.setText(tagInfo.任务名称);
        if("待录音".equals(tagInfo.状态))
        {
            contentViewHolder.tv_statu.setTextColor(mContext.getResources().getColor(R.color.red));
        }
        else
        {
            contentViewHolder.tv_statu.setTextColor(mContext.getResources().getColor(R.color.green));
        }
        contentViewHolder.tv_statu.setText(tagInfo.状态);
        contentViewHolder.tv_time.setText("话术更新于" + tagInfo.更新时间);
        contentViewHolder.tv_progress.setText(tagInfo.完整度);
        contentViewHolder.pb_progress.setProgress(Integer.parseInt(tagInfo.完整度.replace("%","")));
        contentViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(callBack!=null)
                {
                    callBack.onResult(tagInfo.任务ID);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_statu, tv_time, tv_progress;
        ProgressBar pb_progress;

        public ContentViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_statu = (TextView) itemView.findViewById(R.id.tv_statu);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_progress = (TextView) itemView.findViewById(R.id.tv_progress);
            pb_progress = (ProgressBar) itemView.findViewById(R.id.pb_progress);
        }
    }
}
