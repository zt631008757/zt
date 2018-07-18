package com.android.wisdomrecording.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.wisdomrecording.R;
import com.android.wisdomrecording.ui.fragment.Home_Login_Fragment;
import com.android.wisdomrecording.ui.fragment.Home_Recording_Fragment;

public class MainActivity extends BaseActivity {

    public static MainActivity mainActivity;

    public static MainActivity getInstence() {
        return mainActivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.mainActivity = this;
        setContentView(R.layout.activity_main);
        initView();
    }

    LinearLayout ll_recoding, ll_login;
    ImageView img_recoding, img_login;
    TextView tv_recoding, tv_login;
    public int currentIndex = 0;

    Home_Recording_Fragment recodingFregment;
    Home_Login_Fragment loginFragment;


    public void initView() {
        ll_recoding = (LinearLayout) findViewById(R.id.ll_recoding);
        ll_login = (LinearLayout) findViewById(R.id.ll_login);
        img_recoding = (ImageView) findViewById(R.id.img_recoding);
        img_login = (ImageView) findViewById(R.id.img_login);
        tv_recoding = (TextView) findViewById(R.id.tv_recoding);
        tv_login = (TextView) findViewById(R.id.tv_login);

        ll_login.setOnClickListener(this);
        ll_recoding.setOnClickListener(this);

        recodingFregment = new Home_Recording_Fragment();
        loginFragment = new Home_Login_Fragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, loginFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, recodingFregment).commit();


    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.ll_login:
                currentIndex = 1;
                checkFragment();
                break;
            case R.id.ll_recoding:
                currentIndex = 0;
                checkFragment();
                break;
        }
    }

    public void checkFragment() {
        if (currentIndex == 0) {
            img_recoding.setImageResource(R.drawable.ico_bottom_recoding_select);
            img_login.setImageResource(R.drawable.ico_bottom_login_nomol);
            tv_recoding.setTextColor(Color.parseColor("#5bbaff"));
            tv_login.setTextColor(Color.parseColor("#999999"));
            getSupportFragmentManager().beginTransaction().hide(loginFragment).commit();
            getSupportFragmentManager().beginTransaction().show(recodingFregment).commit();
            recodingFregment.checkLogin();
        } else if (currentIndex == 1) {
            img_recoding.setImageResource(R.drawable.ico_bottom_recoding_nomol);
            img_login.setImageResource(R.drawable.ico_bottom_login_select);
            tv_recoding.setTextColor(Color.parseColor("#999999"));
            tv_login.setTextColor(Color.parseColor("#5bbaff"));
            getSupportFragmentManager().beginTransaction().hide(recodingFregment).commit();
            getSupportFragmentManager().beginTransaction().show(loginFragment).commit();
            loginFragment.checkLogin();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(recodingFregment!=null)
        {
            recodingFregment.getData();
        }
    }
}
