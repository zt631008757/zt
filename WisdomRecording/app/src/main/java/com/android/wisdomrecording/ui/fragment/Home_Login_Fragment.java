package com.android.wisdomrecording.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wisdomrecording.R;
import com.android.wisdomrecording.constants.SPConstants;
import com.android.wisdomrecording.manager.ApiManager;
import com.android.wisdomrecording.manager.OkHttpManager;
import com.android.wisdomrecording.responce.BaseResponce;
import com.android.wisdomrecording.responce.LoginResponce;
import com.android.wisdomrecording.tool.SharePreference;
import com.android.wisdomrecording.ui.MainActivity;

import java.security.PrivateKey;

public class Home_Login_Fragment extends Fragment implements View.OnClickListener {

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
        rootView = inflater.inflate(R.layout.fragment_home_login, null);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    TextView tv_login, tv_logout;
    EditText et_phone, et_pwd;
    LinearLayout ll_yidenglu, ll_login;
    TextView tv_gorecoding;

    private void initView() {
        tv_login = rootView.findViewById(R.id.tv_login);
        et_phone = rootView.findViewById(R.id.et_phone);
        et_pwd = rootView.findViewById(R.id.et_pwd);
        ll_login = rootView.findViewById(R.id.ll_login);
        ll_yidenglu = rootView.findViewById(R.id.ll_yidenglu);
        tv_logout = rootView.findViewById(R.id.tv_logout);
        tv_gorecoding = rootView.findViewById(R.id.tv_gorecoding);

        tv_login.setOnClickListener(this);
        tv_logout.setOnClickListener(this);
        tv_gorecoding.setOnClickListener(this);
        checkLogin();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login:
                login();
                break;
            case R.id.tv_logout:
                SharePreference.putValue(mContext, SPConstants.USERID, "");
                checkLogin();
            case R.id.tv_gorecoding:
                MainActivity.getInstence().currentIndex = 0;
                MainActivity.getInstence().checkFragment();
                break;
        }
    }

    private void login() {
        String phone = et_phone.getText().toString().trim();
        String pwd = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(mContext, "请输入手机号", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(mContext, "请输入密码", Toast.LENGTH_LONG).show();
            return;
        }
        ApiManager.login(mContext, phone, pwd, new OkHttpManager.OkHttpCallBack() {
            @Override
            public void onSuccess(BaseResponce baseResponce) {
                LoginResponce responce = (LoginResponce) baseResponce;
                if (!TextUtils.isEmpty(responce.UserID)) {
                    Toast.makeText(mContext, "登录成功", Toast.LENGTH_LONG).show();
                    SharePreference.putValue(mContext, SPConstants.USERID, responce.UserID);
                    checkLogin();
                }
                else
                {
                    Toast.makeText(mContext, "账号或密码错误", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(BaseResponce baseResponce) {
                Toast.makeText(mContext, "请求失败，请重试", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void checkLogin() {
        String uid = SharePreference.getStringValue(mContext, SPConstants.USERID, "");
        if (TextUtils.isEmpty(uid)) {
            //未登录
            ll_yidenglu.setVisibility(View.GONE);
            ll_login.setVisibility(View.VISIBLE);
        } else {
            ll_yidenglu.setVisibility(View.VISIBLE);
            ll_login.setVisibility(View.GONE);
        }
    }
}
