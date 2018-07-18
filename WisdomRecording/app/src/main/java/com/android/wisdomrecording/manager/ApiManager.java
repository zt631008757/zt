package com.android.wisdomrecording.manager;

import android.content.Context;

import com.android.wisdomrecording.constants.ApiConstants;
import com.android.wisdomrecording.interface_.CommCallBack;
import com.android.wisdomrecording.responce.BaseResponce;
import com.android.wisdomrecording.responce.GetTaskResponce;
import com.android.wisdomrecording.responce.HuaShuChildResponce;
import com.android.wisdomrecording.responce.HuaShuDetailReponce;
import com.android.wisdomrecording.responce.HuaShuListResponce;
import com.android.wisdomrecording.responce.LoginResponce;
import com.lzy.okgo.model.HttpMethod;

import java.io.File;

/**
 * Created by Administrator on 2018/7/15 0015.
 */

public class ApiManager {

    //登录
    public static void login(Context context, String phone, String pwd, OkHttpManager.OkHttpCallBack callBack) {
        String url = Config.SERVER_HOST + ApiConstants.login + "?mobile=" + phone + "&password=" + pwd;
        OkHttpManager.okHttpRequest(context, HttpMethod.GET, url, null, null, LoginResponce.class, callBack);
    }

    //获取任务
    public static void renwu(Context context, String UserID, OkHttpManager.OkHttpCallBack callBack) {
        String url = Config.SERVER_HOST + ApiConstants.renwu + "?UserID=" + UserID;
        OkHttpManager.okHttpRequest(context, HttpMethod.GET, url, null, null, GetTaskResponce.class, callBack);
    }

    //获取话术集列表
    public static void huashu_index(Context context, String UserID, String taskId, OkHttpManager.OkHttpCallBack callBack) {
        String url = Config.SERVER_HOST + ApiConstants.huashu_index + "?UserID=" + UserID + "&RenWuID=" + taskId;
        OkHttpManager.okHttpRequest(context, HttpMethod.GET, url, null, null, HuaShuListResponce.class, callBack);
    }

    //获取话术集子集
    public static void huashu(Context context, String UserID, String taskId, String huashuID, String Type, OkHttpManager.OkHttpCallBack callBack) {
        String url = Config.SERVER_HOST + ApiConstants.huashu + "?UserID=" + UserID + "&RenWuID=" + taskId + "&ID=" + huashuID + "&Type=" + Type;
        OkHttpManager.okHttpRequest(context, HttpMethod.GET, url, null, null, HuaShuChildResponce.class, callBack);
    }

    //获取话术详情
    public static void huashu_disp(Context context, String UserID, String taskId, String huashuID, String Type, OkHttpManager.OkHttpCallBack callBack) {
        String url = Config.SERVER_HOST + ApiConstants.huashu_disp + "?UserID=" + UserID + "&RenWuID=" + taskId + "&ID=" + huashuID + "&Type=" + Type;
        OkHttpManager.okHttpRequest(context, HttpMethod.GET, url, null, null, HuaShuDetailReponce.class, callBack);
    }

    //保存话术内容
    public static void huashu_save(Context context, String UserID, String taskId, String huashuID, String content, String Type, OkHttpManager.OkHttpCallBack callBack) {
        String url = Config.SERVER_HOST + ApiConstants.huashu_save + "?UserID=" + UserID + "&RenWuID=" + taskId + "&ID=" + huashuID + "&content=" + content + "&Type=" + Type;
        OkHttpManager.okHttpRequest(context, HttpMethod.GET, url, null, null, BaseResponce.class, callBack);
    }

    //清除全部录音
    public static void del_all(Context context, String UserID, String taskId, OkHttpManager.OkHttpCallBack callBack) {
        String url = Config.SERVER_HOST + ApiConstants.del_all + "?UserID=" + UserID + "&RenWuID=" + taskId;
        OkHttpManager.okHttpRequest(context, HttpMethod.GET, url, null, null, BaseResponce.class, callBack);
    }

    //清除单个录音
    public static void del(Context context, String UserID, String taskId, String huashuID, String Type, OkHttpManager.OkHttpCallBack callBack) {
        String url = Config.SERVER_HOST + ApiConstants.del + "?UserID=" + UserID + "&RenWuID=" + taskId + "&ID=" + huashuID + "&Type=" + Type;
        OkHttpManager.okHttpRequest(context, HttpMethod.GET, url, null, null, BaseResponce.class, callBack);
    }

    //文件上传
    public static void upload(Context context, File file, CommCallBack callBack) {
        String url = Config.SERVER_HOST + ApiConstants.upload;
//        OkHttpManager.okHttpRequest(context,HttpMethod.POST,url,null,null,BaseResponce.class, callBack,file);
        OkHttpManager.Upload(url, file, callBack);
    }

    //保存录音到话术
    public static void save(Context context, String UserID, String taskId, String huashuID, String Type, String video, OkHttpManager.OkHttpCallBack callBack) {
        String url = Config.SERVER_HOST + ApiConstants.save + "?UserID=" + UserID + "&RenWuID=" + taskId + "&ID=" + huashuID + "&Type=" + Type + "&video=" + video;
        OkHttpManager.okHttpRequest(context, HttpMethod.GET, url, null, null, BaseResponce.class, callBack);
    }
}
