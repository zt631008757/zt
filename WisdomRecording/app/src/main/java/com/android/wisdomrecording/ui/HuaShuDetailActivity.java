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
import android.widget.TextView;
import android.widget.Toast;

import com.android.wisdomrecording.R;
import com.android.wisdomrecording.constants.SPConstants;
import com.android.wisdomrecording.interface_.CommCallBack;
import com.android.wisdomrecording.manager.ApiManager;
import com.android.wisdomrecording.manager.OkHttpManager;
import com.android.wisdomrecording.responce.BaseResponce;
import com.android.wisdomrecording.responce.HuaShuDetailReponce;
import com.android.wisdomrecording.tool.SharePreference;
import com.android.wisdomrecording.ui.view.MultiStateView;
import com.android.wisdomrecording.util.MediaPlayerUtil;
import com.android.wisdomrecording.util.Util;

import java.io.File;

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
    TextView tv_title, tv_start_recode, tv_upload;
    ImageView iv_play, iv_delete, iv_edit;
    MultiStateView multiplestatusView;
    TextView tv_name, tv_content, tv_statu, tv_shenyu,tv_save;

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
        tv_upload = (TextView) findViewById(R.id.tv_upload);
        tv_save = (TextView) findViewById(R.id.tv_save);

        iv_play.setOnClickListener(this);
        iv_delete.setOnClickListener(this);
        iv_edit.setOnClickListener(this);
        tv_upload.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        tv_start_recode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    //按下操作
                    case MotionEvent.ACTION_DOWN:
                        Log.i("test", "voicePermission:" + voicePermission());
                        if (voicePermission()) {

                        }
                        recordOperation();
                        break;
                    //松开操作
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        stopRecord();
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
        String uid = SharePreference.getStringValue(mContext, SPConstants.USERID, "");
        ApiManager.huashu_disp(mContext, uid, taskId, huashuId, Type, new OkHttpManager.OkHttpCallBack() {
            @Override
            public void onSuccess(BaseResponce baseResponce) {
                multiplestatusView.setViewState(MultiStateView.ViewState.CONTENT);
                reponce = (HuaShuDetailReponce) baseResponce;
                bindUI();
            }

            @Override
            public void onFailure(BaseResponce baseResponce) {
                multiplestatusView.setViewState(MultiStateView.ViewState.ERROR);
            }
        });
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

//        if ("待录音".equals(reponce.状态)) {
//
//        } else {
//
//        }
        checkStatu();
    }

    private void checkStatu() {
        if ("待录音".equals(reponce.状态)) {
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
                MediaPlayerUtil.play(mAudioFile.getPath());
                break;
            case R.id.iv_delete:
                del();
                break;
            case R.id.iv_edit:
                Intent intent = new Intent(mContext, EditHuaShuActivity.class);
                intent.putExtra("taskId", taskId);
                intent.putExtra("huashuId", huashuId);
                intent.putExtra("content", reponce.内容);
                intent.putExtra("Type", Type);
                startActivity(intent);
                break;
            case R.id.tv_upload:
                upload();
                break;
            case R.id.tv_save:
                save();
                break;
        }
    }

    private void upload()
    {
        File uploadFile = new File(Util.getDiskCacheRootDir(mContext) + "/clock.wav");
        ApiManager.upload(mContext, uploadFile, new CommCallBack() {
            @Override
            public void onResult(Object obj) {

            }
        });
    }

    MediaRecorder mMediaRecorder;
    long startTime = 0;
    long endTime = 0;
    File mAudioFile;

    /**
     * @description 录音操作
     * @author ldm
     * @time 2017/2/9 9:34
     */
    private void recordOperation() {
        //创建MediaRecorder对象
        mMediaRecorder = new MediaRecorder();
        //创建录音文件,.m4a为MPEG-4音频标准的文件的扩展名
        mAudioFile = new File(Util.getDiskCacheRootDir(mContext) + System.currentTimeMillis() + ".m4a");
        //创建父文件夹
        mAudioFile.getParentFile().mkdirs();
        try {
            //创建文件
            mAudioFile.createNewFile();
            //配置mMediaRecorder相应参数
            //从麦克风采集声音数据
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置保存文件格式为MP4
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            //设置采样频率,44100是所有安卓设备都支持的频率,频率越高，音质越好，当然文件越大
            mMediaRecorder.setAudioSamplingRate(44100);
            //设置声音数据编码格式,音频通用格式是AAC
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            //设置编码频率
            mMediaRecorder.setAudioEncodingBitRate(96000);
            //设置录音保存的文件
            mMediaRecorder.setOutputFile(mAudioFile.getAbsolutePath());
            //开始录音
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            //记录开始录音时间
            startTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("test", "Exception:" + e.getMessage());
        }
    }

    /**
     * @description 结束录音操作
     * @author ldm
     * @time 2017/2/9 9:18
     */
    private void stopRecord() {
        if (mMediaRecorder == null) return;
        //停止录音
        mMediaRecorder.stop();
        //记录停止时间
        endTime = System.currentTimeMillis();
        //录音时间处理，比如只有大于2秒的录音才算成功
        int time = (int) ((endTime - startTime) / 1000);
        if (time >= 3) {
            //录音成功,添加数据
        } else {
            mAudioFile = null;
        }
        //录音完成释放资源
        releaseRecorder();
    }

    private boolean voicePermission() {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.
                checkSelfPermission(mContext, android.Manifest.permission.RECORD_AUDIO));
    }


    /**
     * @description 翻放录音相关资源
     * @author ldm
     * @time 2017/2/9 9:33
     */
    private void releaseRecorder() {
        if (null != mMediaRecorder) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }


    private void del() {
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

    private void save()
    {
        String uid = SharePreference.getStringValue(mContext, SPConstants.USERID, "");
        ApiManager.save(mContext, uid, taskId, huashuId, Type, "20180718132206.wav", new OkHttpManager.OkHttpCallBack() {
            @Override
            public void onSuccess(BaseResponce baseResponce) {

            }

            @Override
            public void onFailure(BaseResponce baseResponce) {

            }
        });
    }
}
