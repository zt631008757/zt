package com.android.wisdomrecording.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.wisdomrecording.R;

/**
 * Created by Administrator on 2018/6/20.
 */

public class A_ModelActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        getData();
    }

    private void initView() {
    }

    private void getData() {

    }
}
