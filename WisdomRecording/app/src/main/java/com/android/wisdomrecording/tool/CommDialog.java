package com.android.wisdomrecording.tool;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.android.wisdomrecording.R;

/**
 * Created by Administrator on 2018/6/22.
 */

public class CommDialog {


    /**
     * by zt   全局通用样式弹框
     *
     * @param context
     * @param title               对话框标题， 为空不显示
     * @param btnOkStr            确认按钮文字
     * @param btnCancelStr        取消按钮文字， 为空 不显示取消按钮
     * @param msg                 对话框内容
     * @param cancelClickListener 取消按钮点击事件
     * @param okClickListener     确认按钮点击事件
     *                            //     * @param property            对话框属性，可选参数
     * @return
     */
    public static Dialog showCommDialog(Context context, String title, String btnOkStr, String btnCancelStr, String msg, final View.OnClickListener cancelClickListener, final View.OnClickListener okClickListener) {
        final Dialog[] dialog = {new Dialog(context, R.style.myDialog)};
        dialog[0].setContentView(R.layout.dialog_comm);
        TextView tv_title = (TextView) dialog[0].findViewById(R.id.tv_title); //标题
        TextView content_text = (TextView) dialog[0].findViewById(R.id.content_text);  //提示文字
        TextView pop_ok = (TextView) dialog[0].findViewById(R.id.pop_ok);  //单个按钮文字
        TextView pop_cancel = (TextView) dialog[0].findViewById(R.id.pop_cancel);  //两个按钮 左边取消按钮
        TextView pop_comit = (TextView) dialog[0].findViewById(R.id.pop_comit);  //两个按钮 左边取消按钮

        content_text.setMovementMethod(ScrollingMovementMethod.getInstance());
        if (!TextUtils.isEmpty(title)) {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(title);
        }
        if (!TextUtils.isEmpty(msg)) {
//                if (msg.length() < 15) {
//                    content_text.setGravity(Gravity.CENTER);
//                } else {
//                    content_text.setGravity(Gravity.LEFT);
//                }
            content_text.setText(msg);
        }
        if (!TextUtils.isEmpty(btnOkStr) && TextUtils.isEmpty(btnCancelStr))    //只有一个按钮
        {
            pop_ok.setVisibility(View.VISIBLE);
            pop_cancel.setVisibility(View.GONE);
            pop_comit.setVisibility(View.GONE);
            pop_ok.setText(btnOkStr);
        } else if (!TextUtils.isEmpty(btnOkStr) && !TextUtils.isEmpty(btnCancelStr))   //有两个按钮
        {
            pop_ok.setVisibility(View.GONE);
            pop_cancel.setVisibility(View.VISIBLE);
            pop_comit.setVisibility(View.VISIBLE);
            pop_cancel.setText(btnCancelStr);
            pop_comit.setText(btnOkStr);
        }

        pop_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog[0].dismiss();
                if (okClickListener != null)
                    okClickListener.onClick(v);
            }
        });
        pop_comit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog[0].dismiss();
                if (okClickListener != null)
                    okClickListener.onClick(v);
            }
        });
        pop_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog[0].dismiss();
                if (cancelClickListener != null)
                    cancelClickListener.onClick(v);
            }
        });

        dialog[0].setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog1) {
                dialog[0] = null;
            }
        });
        dialog[0].show();
        return dialog[0];
    }

}
