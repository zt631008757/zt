package com.android.wisdomrecording.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.wisdomrecording.R;
import com.android.wisdomrecording.adapter.TaskListAdapter;
import com.android.wisdomrecording.constants.SPConstants;
import com.android.wisdomrecording.interface_.CommCallBack;
import com.android.wisdomrecording.manager.ApiManager;
import com.android.wisdomrecording.manager.OkHttpManager;
import com.android.wisdomrecording.responce.BaseResponce;
import com.android.wisdomrecording.responce.GetTaskResponce;
import com.android.wisdomrecording.tool.SharePreference;
import com.android.wisdomrecording.ui.HuaShuListAcitivity;
import com.android.wisdomrecording.ui.MainActivity;
import com.android.wisdomrecording.ui.view.MultiStateView;

public class Home_Recording_Fragment extends Fragment implements View.OnClickListener {


    View rootView;
    Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home_recoding, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    TextView tv_gologin;
    LinearLayout ll_unlogin, ll_main;
    GetTaskResponce responce;
    RecyclerView recycleview;
    TaskListAdapter adapter;
    MultiStateView multiplestatusView;

    private void initView() {
        tv_gologin = (TextView) rootView.findViewById(R.id.tv_gologin);
        tv_gologin.setOnClickListener(this);
        ll_unlogin = rootView.findViewById(R.id.ll_unlogin);
        ll_main = rootView.findViewById(R.id.ll_main);
        recycleview = rootView.findViewById(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(mContext));

        adapter = new TaskListAdapter(mContext, callback);
        recycleview.setAdapter(adapter);
        checkLogin();

        multiplestatusView = (MultiStateView) rootView.findViewById(R.id.multiplestatusView);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_gologin:
                MainActivity.getInstence().currentIndex = 1;
                MainActivity.getInstence().checkFragment();
                break;
        }
    }

    public void getData() {
        String uid = SharePreference.getStringValue(mContext, SPConstants.USERID, "");
        ApiManager.renwu(mContext, uid, new OkHttpManager.OkHttpCallBack() {
            @Override
            public void onSuccess(BaseResponce baseResponce) {

                responce = (GetTaskResponce) baseResponce;
                if ("OK".equals(responce.result)) {
                    multiplestatusView.setViewState(MultiStateView.ViewState.CONTENT);
                    bindUI();
                }
                else
                {
                    multiplestatusView.setViewState(MultiStateView.ViewState.ERROR);
                }
            }

            @Override
            public void onFailure(BaseResponce baseResponce) {
                multiplestatusView.setViewState(MultiStateView.ViewState.ERROR);
            }
        });
    }

    public void bindUI() {
        if (responce.data != null) {
            adapter.setData(responce.data);
        }
    }

    CommCallBack callback = new CommCallBack() {
        @Override
        public void onResult(Object obj) {
            String taskId = (String) obj;
            Intent intent = new Intent(mContext, HuaShuListAcitivity.class);
            intent.putExtra("taskId", taskId);
            startActivity(intent);
        }
    };


    public void checkLogin() {
        String uid = SharePreference.getStringValue(mContext, SPConstants.USERID, "");
        if (TextUtils.isEmpty(uid)) {
            //未登录
            ll_main.setVisibility(View.GONE);
            ll_unlogin.setVisibility(View.VISIBLE);
        } else {
            //已登录
            ll_main.setVisibility(View.VISIBLE);
            ll_unlogin.setVisibility(View.GONE);
            getData();
        }
    }
}
