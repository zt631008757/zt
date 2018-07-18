package com.android.wisdomrecording.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wisdomrecording.R;
import com.android.wisdomrecording.adapter.HuaShuListAdapter;
import com.android.wisdomrecording.bean.HuaShuInfo;
import com.android.wisdomrecording.constants.SPConstants;
import com.android.wisdomrecording.interface_.CommCallBack;
import com.android.wisdomrecording.manager.ApiManager;
import com.android.wisdomrecording.manager.OkHttpManager;
import com.android.wisdomrecording.responce.BaseResponce;
import com.android.wisdomrecording.responce.HuaShuListResponce;
import com.android.wisdomrecording.tool.SharePreference;
import com.android.wisdomrecording.ui.view.MultiStateView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/15 0015.
 */

public class HuaShuListAcitivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskId = getIntent().getStringExtra("taskId");
        setContentView(R.layout.activity_huashulist);
        initView();
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    String taskId;
    MultiStateView multiplestatusView;
    RecyclerView recycleview;
    TextView tv_progress, tv_beginluyin, tv_title;
    TextView tv_dailu, tv_yilu, tv_deleteall;

    HuaShuListResponce responce;
    HuaShuListAdapter adapter;

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("选择话术集");

        tv_progress = (TextView) findViewById(R.id.tv_progress);
        tv_beginluyin = (TextView) findViewById(R.id.tv_beginluyin);
        tv_dailu = (TextView) findViewById(R.id.tv_dailu);
        tv_yilu = (TextView) findViewById(R.id.tv_yilu);
        tv_deleteall = (TextView) findViewById(R.id.tv_deleteall);

        multiplestatusView = (MultiStateView) findViewById(R.id.multiplestatusView);
        multiplestatusView.setViewState(MultiStateView.ViewState.LOADING);
        multiplestatusView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiplestatusView.setViewState(MultiStateView.ViewState.LOADING);
                getData();
            }
        });

        recycleview = (RecyclerView) findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new HuaShuListAdapter(mContext, callback);
        recycleview.setAdapter(adapter);

        tv_beginluyin.setOnClickListener(this);
        tv_deleteall.setOnClickListener(this);
        findViewById(R.id.public_title_left_img).setOnClickListener(this);
    }

    private void getData() {
        String uid = SharePreference.getStringValue(mContext, SPConstants.USERID, "");
        ApiManager.huashu_index(mContext, uid, taskId, new OkHttpManager.OkHttpCallBack() {
            @Override
            public void onSuccess(BaseResponce baseResponce) {
                multiplestatusView.setViewState(MultiStateView.ViewState.CONTENT);
                responce = (HuaShuListResponce) baseResponce;
                bindData();
            }

            @Override
            public void onFailure(BaseResponce baseResponce) {
                multiplestatusView.setViewState(MultiStateView.ViewState.ERROR);
            }
        });
    }

    private void bindData() {
        tv_progress.setText(responce.完整度);
        tv_dailu.setText(responce.待录);
        tv_yilu.setText(responce.已录);

        //列表
        List<HuaShuInfo> list = new ArrayList<>();
        if (responce.话术 != null && responce.话术.size() > 0) {
            HuaShuInfo title = new HuaShuInfo();
            title.isTitle = true;
            title.titleName = "话术";
            list.add(title);
            list.addAll(responce.话术);
        }
        if (responce.知识库 != null && responce.知识库.size() > 0) {
            HuaShuInfo title = new HuaShuInfo();
            title.isTitle = true;
            title.titleName = "知识库";
            list.add(title);
            list.addAll(responce.知识库);
        }
        adapter.setData(list);
    }

    CommCallBack callback = new CommCallBack() {
        @Override
        public void onResult(Object obj) {
            HuaShuInfo info = (HuaShuInfo) obj;
            String id = "";
            if (!TextUtils.isEmpty(info.话术ID)) {
                id = info.话术ID;
            } else if (!TextUtils.isEmpty(info.知识库ID)) {
                id = info.知识库ID;
            }
            Intent intent = new Intent(mContext, HuaShuChildListActivity.class);
            intent.putExtra("huashuId", id);
            intent.putExtra("taskId", taskId);
            intent.putExtra("Type", info.Type);
            startActivity(intent);
        }
    };

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_beginluyin:
                Intent intent = new Intent(mContext, HuaShuDetailActivity.class);
                intent.putExtra("taskId", taskId);
                startActivity(intent);
                break;
            case R.id.public_title_left_img:
                finish();
                break;
            case R.id.tv_deleteall:
                tv_deleteall();
                break;
        }
    }

    public void tv_deleteall()
    {
        String uid = SharePreference.getStringValue(mContext, SPConstants.USERID, "");
        ApiManager.del_all(mContext, uid, taskId, new OkHttpManager.OkHttpCallBack() {
            @Override
            public void onSuccess(BaseResponce baseResponce) {
                if("OK".equals(baseResponce.result))
                {
                    Toast.makeText(mContext,"清除成功",Toast.LENGTH_SHORT).show();
                }
                getData();
            }

            @Override
            public void onFailure(BaseResponce baseResponce) {
                Toast.makeText(mContext,"请求失败，请重试",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
