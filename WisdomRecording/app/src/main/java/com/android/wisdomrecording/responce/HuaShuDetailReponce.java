package com.android.wisdomrecording.responce;

import com.android.wisdomrecording.bean.HuaShuChildInfo;

import java.util.List;

/**
 * Created by Administrator on 2018/7/15 0015.
 */

public class HuaShuDetailReponce extends BaseResponce {
    public String 名称;// ":"无法应答处理",
    public String 内容;//":"抱歉，暂时没听清您的问题。你刚刚说什么来着？",
    public String 状态;//":"未录音",
    public String 剩余录音数;//":"25"
    public String 录音文件名;

    public List<HuaShuChildInfo> data;
}
