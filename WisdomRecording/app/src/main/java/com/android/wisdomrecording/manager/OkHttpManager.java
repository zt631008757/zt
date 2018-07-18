package com.android.wisdomrecording.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.internal.http.multipart.FilePart;
import com.android.internal.http.multipart.MultipartEntity;
import com.android.internal.http.multipart.Part;
import com.android.wisdomrecording.interface_.CommCallBack;
import com.android.wisdomrecording.responce.BaseResponce;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.HttpMethod;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.BodyRequest;
import com.lzy.okgo.request.base.Request;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2018/6/13.
 */

public class OkHttpManager {

    public interface OkHttpCallBack {
        void onSuccess(BaseResponce baseResponce);

        void onFailure(BaseResponce baseResponce);
    }

    /**
     * okHttp请求， 外部调用
     *
     * @param context       上下文，用于取消请求
     * @param methed        请求类型 (HttpMethod.POST/HttpMethod.GET)
     * @param url           请求地址
     * @param urlParams     get方式 拼接参数
     * @param requestBody   post方式 放入请求体的参数
     * @param responseClass 返回responce类型 （自动解析json）
     * @param callBack      回调对象
     */
    public static void okHttpRequest(Context context, HttpMethod methed, String url, List<String> urlParams, Object requestBody, Class<? extends BaseResponce> responseClass, OkHttpCallBack callBack, File... file) {
        //url拼接参数
        if (urlParams != null && urlParams.size() > 0) {
            for (String param : urlParams) {
                url += "/" + param;
            }
        }
        //body参数
        String bodyJson = "";
        if (requestBody != null) {
            bodyJson = new Gson().toJson(requestBody);
        }
        executeByOkHttp(methed, context, url, bodyJson, null, responseClass, callBack, file);
    }

    private static void executeByOkHttp(HttpMethod methed, final Context context, final String url, String bodyJson, Map<String, String> headers, final Class<? extends BaseResponce> responseClass, final OkHttpCallBack callBack, File... file) {
        try {
            Request request = null;
            //添加body
            if (HttpMethod.POST == methed) {
                request = OkGo.post(url).tag(context);
                if (file.length > 0)
                    ((BodyRequest) request).addFileParams("file", Arrays.asList(file));
                BodyRequest bodyRequest = (BodyRequest) request;
            } else {
                request = OkGo.get(url).tag(context);
            }
            //添加headers
            if (headers != null && headers.size() > 0) {
                for (String key : headers.keySet()) {
                    String value = headers.get(key);
                    request.getHeaders().put(key, value);
                }
            }
            request.execute(new AbsCallback<BaseResponce>() {

                @Override
                public BaseResponce convertResponse(okhttp3.Response response) throws Throwable {
                    //子线程
                    BaseResponce object = null;
                    try {
                        String s = response.body().string();
                        if (responseClass != null) {
                            object = new Gson().fromJson(s, responseClass);
                        }
//                        else {
//                            object = new BaseResponce();
//                            object.code = "10000";
//                        }
//                        object.result = s;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return object;
                }

                @Override
                public void onSuccess(Response<BaseResponce> response) {
                    if (callBack == null) return;
                    //UI线程
                    if (response.body() == null)  //数据解析失败
                    {
                        callBack.onFailure(response.body());
                    } else {           //数据解析成功
                        callBack.onSuccess(response.body());
                    }
                }

                @Override
                public void onError(Response<BaseResponce> response) {
                    super.onError(response);
                    if (callBack != null) {
                        callBack.onFailure(null);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (callBack != null) {
                callBack.onFailure(null);
            }
        }
    }


    public static void Upload(final String url, final File file,final CommCallBack callBack) {
        final AsyncTask<Object, Object, String> task = new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object[] params) {
                String result = "";
                //创建httpRequest对象
                HttpPost httpRequest = new HttpPost(url);
                try {
                    if (file != null) {
                        Part part = new FilePart("file", file);
                        MultipartEntity entity = new MultipartEntity(new Part[]{part});
                        httpRequest.setEntity(entity);
                    }
//            httpRequest.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
//            httpRequest.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);

                    //取得HttpClient对象
                    HttpClient httpclient = new DefaultHttpClient();
                    //请求HttpClient，取得HttpResponse
                    HttpResponse httpResponse = httpclient.execute(httpRequest);
                    //请求成功
                    if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        //取得返回的字符串
                        result = EntityUtils.toString(httpResponse.getEntity());
                    } else {
                        result = "失败";
                    }
                    //文件 文件上传 完成 删除本地缓存
//                    if (file != null) {
//                        file.delete();
//                    }
                    httpclient.getConnectionManager().shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "失败";
                }
                Log.i("test","result:"+result);
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                callBack.onResult(result);
//                if ("失败".equals(result)) {
//                    callBack.onFailure();
//                } else {
//                    callBack.onSuccess(result);
//                }
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
