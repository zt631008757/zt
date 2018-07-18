package com.android.wisdomrecording.responce;

import com.android.wisdomrecording.bean.HuaShuInfo;

import java.util.List;

/**
 * Created by Administrator on 2018/7/15 0015.
 */

public class HuaShuChildListResponce extends BaseResponce{
    public String 任务ID;
    public String 完整度;
    public List<HuaShuInfo> 话术;
    public List<HuaShuInfo> 知识库;
}
