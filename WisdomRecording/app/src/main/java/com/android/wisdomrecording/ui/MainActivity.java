package com.android.wisdomrecording.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wisdomrecording.R;
import com.android.wisdomrecording.manager.ApiManager;
import com.android.wisdomrecording.manager.OkHttpManager;
import com.android.wisdomrecording.responce.BaseResponce;
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
        getData();
    }

    private void getData() {
        ApiManager.enable(mContext, new OkHttpManager.OkHttpCallBack() {
            @Override
            public void onSuccess(BaseResponce baseResponce) {
                String result = baseResponce.result.replace("\n","");
                if("true".equals(result))
                {
                    tv_guid.setVisibility(View.GONE);
                }
                else if("false".equals(result))
                {
                    tv_guid.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(BaseResponce baseResponce) {

            }
        });
    }

    LinearLayout ll_recoding, ll_login;
    ImageView img_recoding, img_login;
    TextView tv_recoding, tv_login;
    TextView tv_guid;
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
        tv_guid  = (TextView) findViewById(R.id.tv_guid);

        ll_login.setOnClickListener(this);
        ll_recoding.setOnClickListener(this);
        tv_guid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
