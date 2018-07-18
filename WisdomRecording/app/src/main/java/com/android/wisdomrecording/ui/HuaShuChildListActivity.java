package com.android.wisdomrecording.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.wisdomrecording.R;
import com.android.wisdomrecording.adapter.HuaShuChildListAdapter;
import com.android.wisdomrecording.bean.HuaShuChildInfo;
import com.android.wisdomrecording.bean.HuaShuInfo;
import com.android.wisdomrecording.constants.SPConstants;
import com.android.wisdomrecording.interface_.CommCallBack;
import com.android.wisdomrecording.manager.ApiManager;
import com.android.wisdomrecording.manager.OkHttpManager;
import com.android.wisdomrecording.responce.BaseResponce;
import com.android.wisdomrecording.responce.HuaShuChildResponce;
import com.android.wisdomrecording.tool.SharePreference;
import com.android.wisdomrecording.ui.view.MultiStateView;
import com.android.wisdomrecording.util.MediaPlayerUtil;

/**
 * Created by Administrator on 2018/6/20.
 */

public class HuaShuChildListActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huashu_childlist);
        taskId = getIntent().getStringExtra("taskId");
        huashuId = getIntent().getStringExtra("huashuId");
        Type = getIntent().getStringExtra("Type");
        initView();
//        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    String huashuId;
    String taskId;
    String Type;
    HuaShuChildResponce responce;


    MultiStateView multiplestatusView;
    RecyclerView recycleview;
    HuaShuChildListAdapter adapter;
    TextView tv_title;

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("话术列表");
        findViewById(R.id.public_title_left_img).setOnClickListener(this);
        recycleview = (RecyclerView) findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new HuaShuChildListAdapter(mContext, callBack);
        recycleview.setAdapter(adapter);


        multiplestatusView = (MultiStateView) findViewById(R.id.multiplestatusView);
        multiplestatusView.setViewState(MultiStateView.ViewState.LOADING);
        multiplestatusView.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiplestatusView.setViewState(MultiStateView.ViewState.LOADING);
                getData();
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.public_title_left_img:
                finish();
                break;
        }
    }

    private void getData() {
        String uid = SharePreference.getStringValue(mContext, SPConstants.USERID, "");
        ApiManager.huashu(mContext, uid, taskId, huashuId, Type,new OkHttpManager.OkHttpCallBack() {
            @Override
            public void onSuccess(BaseResponce baseResponce) {
                responce = (HuaShuChildResponce) baseResponce;
                if ("OK".equals(responce.result)) {
                    multiplestatusView.setViewState(MultiStateView.ViewState.CONTENT);
                    bindUI();
                } else {
                    multiplestatusView.setViewState(MultiStateView.ViewState.ERROR);
                }
            }

            @Override
            public void onFailure(BaseResponce baseResponce) {
                multiplestatusView.setViewState(MultiStateView.ViewState.ERROR);
            }
        });
    }

    CommCallBack callBack = new CommCallBack() {
        @Override
        public void onResult(Object obj) {
            HuaShuChildInfo info = (HuaShuChildInfo) obj;
            Intent intent = new Intent(mContext, HuaShuDetailActivity.class);
            intent.putExtra("taskId", taskId);
            intent.putExtra("huashuId", info.ID);
            intent.putExtra("Type", info.Type);
            startActivity(intent);
        }
    };

    private void bindUI() {
        adapter.setData(responce.data);
    }
}
