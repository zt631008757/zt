package com.android.wisdomrecording.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wisdomrecording.R;
import com.android.wisdomrecording.constants.SPConstants;
import com.android.wisdomrecording.manager.ApiManager;
import com.android.wisdomrecording.manager.OkHttpManager;
import com.android.wisdomrecording.responce.BaseResponce;
import com.android.wisdomrecording.tool.SharePreference;
import com.android.wisdomrecording.util.MediaPlayerUtil;

/**
 * Created by Administrator on 2018/6/20.
 */

public class EditHuaShuActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskId = getIntent().getStringExtra("taskId");
        huashuId = getIntent().getStringExtra("huashuId");
        content = getIntent().getStringExtra("content");
        Type = getIntent().getStringExtra("Type");
        setContentView(R.layout.activity_edithuashu);
        initView();
    }

    String taskId="";
    String huashuId="";
    String content ="";
    String Type="";

    TextView tv_title,tv_save;
    EditText tv_content;
    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("编辑话术");
        tv_content = (EditText) findViewById(R.id.tv_content);
        findViewById(R.id.public_title_left_img).setOnClickListener(this);
        tv_save  = (TextView) findViewById(R.id.tv_save);
        tv_save.setOnClickListener(this);

        tv_content.setText(content);
        tv_content.setSelection(content.length());
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.public_title_left_img:
                finish();
                break;
            case R.id.tv_save:
                save();
                break;
        }
    }

    private void save()
    {
        String str = tv_content.getText().toString().trim();
        if(TextUtils.isEmpty(str))
        {
            Toast.makeText(mContext,"不能为空哦",Toast.LENGTH_SHORT).show();
            return;
        }
        tv_save.setEnabled(false);
        String uid = SharePreference.getStringValue(mContext, SPConstants.USERID, "");
        ApiManager.huashu_save(mContext, uid, taskId, huashuId, str,Type, new OkHttpManager.OkHttpCallBack() {
            @Override
            public void onSuccess(BaseResponce baseResponce) {
                tv_save.setEnabled(true);
                if("OK".equals(baseResponce.result))
                {
                    Toast.makeText(mContext,"修改成功",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(BaseResponce baseResponce) {
                Toast.makeText(mContext, "请求失败，请重试", Toast.LENGTH_LONG).show();
                tv_save.setEnabled(true);
            }
        });
    }
}
