package com.android.wisdomrecording.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wisdomrecording.R;
import com.android.wisdomrecording.bean.HuaShuChildInfo;
import com.android.wisdomrecording.constants.SPConstants;
import com.android.wisdomrecording.interface_.CommCallBack;
import com.android.wisdomrecording.manager.ApiManager;
import com.android.wisdomrecording.manager.OkHttpManager;
import com.android.wisdomrecording.responce.BaseResponce;
import com.android.wisdomrecording.responce.FileUploadResonce;
import com.android.wisdomrecording.responce.HuaShuDetailReponce;
import com.android.wisdomrecording.tool.CommDialog;
import com.android.wisdomrecording.tool.SharePreference;
import com.android.wisdomrecording.ui.view.MultiStateView;
import com.android.wisdomrecording.util.AudioFileFunc;
import com.android.wisdomrecording.util.AudioRecordUtil;
import com.android.wisdomrecording.util.ErrorCode;
import com.android.wisdomrecording.util.MediaPlayerUtil;
import com.android.wisdomrecording.util.Util;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2018/6/20.
 */

public class HuaShuDetailActivity extends BaseActivity {

    String taskId = "";
    String huashuId = "";
    String Type = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskId = getIntent().getStringExtra("taskId");
        huashuId = getIntent().getStringExtra("huashuId");
        Type = getIntent().getStringExtra("Type");
        setContentView(R.layout.activity_huashudetail);
        initView();
//        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    HuaShuDetailReponce reponce;
    TextView tv_title, tv_start_recode;
    ImageView iv_play, iv_delete, iv_edit;
    MultiStateView multiplestatusView;
    TextView tv_name, tv_content, tv_statu, tv_shenyu, tv_save;
    LinearLayout ll_left, ll_right;
    RelativeLayout rl_loading;

    AudioRecordUtil util;
    String fileName = "";
    List<HuaShuChildInfo> list;
    int currentIndex = 0;

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("话术详情");
        findViewById(R.id.public_title_left_img).setOnClickListener(this);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_statu = (TextView) findViewById(R.id.tv_statu);
        tv_shenyu = (TextView) findViewById(R.id.tv_shenyu);
        tv_start_recode = (TextView) findViewById(R.id.tv_start_recode);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        iv_edit = (ImageView) findViewById(R.id.iv_edit);
        tv_save = (TextView) findViewById(R.id.tv_save);
        ll_left = (LinearLayout) findViewById(R.id.ll_left);
        ll_right = (LinearLayout) findViewById(R.id.ll_right);
        rl_loading = (RelativeLayout) findViewById(R.id.rl_loading);
        rl_loading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        iv_play.setOnClickListener(this);
        iv_delete.setOnClickListener(this);
        iv_edit.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        ll_left.setOnClickListener(this);
        ll_right.setOnClickListener(this);
        tv_start_recode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                File file;
                switch (motionEvent.getAction()) {
                    //按下操作
                    case MotionEvent.ACTION_DOWN:
                        file = new File(AudioFileFunc.getWavFilePath());
                        if (file.exists()) {
                            file.delete();
                        }
                        util = new AudioRecordUtil();

                        int result = util.startRecordAndFile();
                        if (result == ErrorCode.SUCCESS) {
                            //正常录制
                            tv_start_recode.setText("松开结束");
                            tv_start_recode.setBackgroundResource(R.drawable.shape_btn_round_maincolor);
                            tv_start_recode.setTextColor(Color.parseColor("#ffffff"));
                        } else {
                            file = new File(AudioFileFunc.getWavFilePath());
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                        break;
                    //松开操作
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        util.stopRecordAndFile();
                        tv_start_recode.setText("按住说话");
                        tv_start_recode.setBackgroundResource(R.drawable.shape_btn_round_blue_withstcoke);
                        tv_start_recode.setTextColor(getResources().getColor(R.color.mainColor));

                        //判断录制结果
                        long fileSize = util.getRecordFileSize();
                        if (fileSize <= 7148) {
                            break;
                        } else if (fileSize < 100000) {
                            Toast.makeText(mContext, "录制时间太短，请重新录制", Toast.LENGTH_SHORT).show();
                            break;
                        }

                        file = new File(AudioFileFunc.getWavFilePath());
                        if (file.exists()) {
                            //上传操作
                            upload(file);
                        }
                        break;
                }
                //对OnTouch事件做了处理，返回true
                return true;
            }
        });

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

    private void getData() {
        fileName = "";
        String uid = SharePreference.getStringValue(mContext, SPConstants.USERID, "");
        ApiManager.huashu_disp(mContext, uid, taskId, huashuId, Type, new OkHttpManager.OkHttpCallBack() {
            @Override
            public void onSuccess(BaseResponce baseResponce) {
                hideLoading();
                multiplestatusView.setViewState(MultiStateView.ViewState.CONTENT);
                reponce = (HuaShuDetailReponce) baseResponce;
                if (reponce.data != null) {
                    list = reponce.data;
                }
                bindUI();
            }

            @Override
            public void onFailure(BaseResponce baseResponce) {
                rl_loading.setVisibility(View.GONE);
                multiplestatusView.setViewState(MultiStateView.ViewState.ERROR);
            }
        });
    }

    private void showLoading(String text) {
        TextView tv_loading = (TextView) findViewById(R.id.tv_loading);
        tv_loading.setText(text);
        rl_loading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        rl_loading.setVisibility(View.GONE);
    }

    private void bindUI() {
        tv_name.setText(reponce.名称);
        tv_content.setText(reponce.内容);
        tv_statu.setText(reponce.状态);
        tv_shenyu.setText("剩余" + reponce.剩余录音数 + "条待录音");

        if ("待录音".equals(reponce.状态)) {
            tv_statu.setTextColor(mContext.getResources().getColor(R.color.red));
        } else {
            tv_statu.setTextColor(mContext.getResources().getColor(R.color.green));
        }

        checkStatu();
    }

    private void checkStatu() {
        if (!TextUtils.isEmpty(fileName)) {
            iv_play.setEnabled(true);
            iv_play.setImageResource(R.drawable.ico_voice);
            iv_delete.setEnabled(true);
            iv_delete.setImageResource(R.drawable.ico_delete);
            tv_start_recode.setEnabled(false);
            tv_start_recode.setBackgroundResource(R.drawable.shape_btn_round_grey_withstcoke);
            tv_start_recode.setTextColor(Color.parseColor("#999999"));
        } else if ("待录音".equals(reponce.状态)) {   //
            iv_play.setEnabled(false);
            iv_play.setImageResource(R.drawable.ico_voice_grey);
            iv_delete.setEnabled(false);
            iv_delete.setImageResource(R.drawable.ico_delete_grey);

            tv_start_recode.setEnabled(true);
            tv_start_recode.setBackgroundResource(R.drawable.shape_btn_round_blue_withstcoke);
            tv_start_recode.setTextColor(getResources().getColor(R.color.mainColor));
        } else {
            iv_play.setEnabled(true);
            iv_play.setImageResource(R.drawable.ico_voice);
            iv_delete.setEnabled(true);
            iv_delete.setImageResource(R.drawable.ico_delete);

            tv_start_recode.setEnabled(false);
            tv_start_recode.setBackgroundResource(R.drawable.shape_btn_round_grey_withstcoke);
            tv_start_recode.setTextColor(Color.parseColor("#999999"));
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.public_title_left_img:
                finish();
                break;
            case R.id.iv_play:
                File file = new File(AudioFileFunc.getWavFilePath());
                if (!TextUtils.isEmpty(fileName) && file.exists()) {
                    MediaPlayerUtil.play(file.getPath());
                } else if (!TextUtils.isEmpty(reponce.录音文件名)) {
                    MediaPlayerUtil.play(reponce.录音文件名);
//                    MediaPlayerUtil.play("http://fjdx.sc.chinaz.com/Files/DownLoad/sound1/201606/7351.wav");
                }
                break;
            case R.id.iv_edit:
                Intent intent = new Intent(mContext, EditHuaShuActivity.class);
                intent.putExtra("taskId", taskId);
                intent.putExtra("huashuId", huashuId);
                intent.putExtra("content", reponce.内容);
                intent.putExtra("Type", Type);
                startActivity(intent);
                break;
            case R.id.tv_save:
                save();
            case R.id.iv_delete:
                del();
                break;
            case R.id.ll_left:
                if (currentIndex == 0) {
                    Toast.makeText(mContext, "没有上一条了", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentIndex--;
                huashuId = list.get(currentIndex).ID;
                Type = list.get(currentIndex).Type;
                showLoading("加载中");
                getData();
                break;
            case R.id.ll_right:
                if (currentIndex == list.size() - 1) {
                    Toast.makeText(mContext, "没有下一条了", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentIndex++;
                huashuId = list.get(currentIndex).ID;
                Type = list.get(currentIndex).Type;
                showLoading("加载中");
                getData();
                break;
        }
    }


    private void upload(File file) {
        showLoading("上传录音中");
        ApiManager.upload(mContext, file, new CommCallBack() {
            @Override
            public void onResult(Object obj) {
                hideLoading();
                String result = (String) obj;
                // {"code":"0","msg":"上传成功","filename":"./video/20180719002126.wav"}
                FileUploadResonce fileUploadResonce = new Gson().fromJson(result, FileUploadResonce.class);
                if ("上传成功".equals(fileUploadResonce.msg)) {
                    Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
                    fileName = fileUploadResonce.filename;
                    checkStatu();
                } else {
                    Toast.makeText(mContext, "上传失败：" + fileUploadResonce.msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void del() {
        CommDialog.showCommDialog(mContext, "", "确定", "取消", "确定删除吗？", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(fileName)) {
                    fileName = "";
                    checkStatu();
                } else {
                    String uid = SharePreference.getStringValue(mContext, SPConstants.USERID, "");
                    ApiManager.del(mContext, uid, taskId, huashuId, Type, new OkHttpManager.OkHttpCallBack() {
                        @Override
                        public void onSuccess(BaseResponce baseResponce) {
                            if ("OK".equals(baseResponce.result)) {
                                Toast.makeText(mContext, "已删除录音", Toast.LENGTH_SHORT).show();
                            }
                            getData();
                        }

                        @Override
                        public void onFailure(BaseResponce baseResponce) {

                        }
                    });
                }
            }
        });
    }

    private void save() {
        if (!TextUtils.isEmpty(fileName)) {
            String uid = SharePreference.getStringValue(mContext, SPConstants.USERID, "");
            ApiManager.save(mContext, uid, taskId, huashuId, Type, fileName, new OkHttpManager.OkHttpCallBack() {
                @Override
                public void onSuccess(BaseResponce baseResponce) {
                    if ("OK".equals(baseResponce.result.toUpperCase())) {
                        Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                        getData();
                    }
                }

                @Override
                public void onFailure(BaseResponce baseResponce) {
                    Toast.makeText(mContext, "请求失败，请重试", Toast.LENGTH_SHORT).show();
                }
            });
        } else if ("已录音".equals(reponce.状态)) {
            Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "存在待录制话术", Toast.LENGTH_SHORT).show();
        }
    }
}
