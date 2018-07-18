package com.android.wisdomrecording.manager;

/**
 * Created by zt on 2018/6/13.
 */

public class Config {

    // 环境
    public static int IS_ALPHA = 100;
    public static int IS_BATE = 101;
    public static int IS_PREVIEW = 102;
    public static int IS_RELEASE = 103;
    public static int curVersion = IS_ALPHA;

    public static String SERVER_HOST ="";    //域名地址


    static
    {
        if(curVersion==IS_RELEASE)
        {
            //正式环境
            SERVER_HOST ="";
        }
        else
        {
            //测试环境
            SERVER_HOST ="http://crm.yunzuoxi.com/luyin";
        }
    }

}
