package com.Jayce.Redis.Utils;

/**
 * 类名称：
 * 类描述：
 * <p>
 * 创建人： KevinHui
 * <p>
 * 创建时间：2018/8/21 10:40
 * <p>
 * 修改人：       修改时间：       修改备注：
 * <p>
 * <p>
 * Copyright (c) 2017 厦门自贸试验区电子口岸有限公司-版权所有
 */
public class RedisConfig {
    //获取最大数量
    public static int getMaxTotal(){
        return 1000;
    }

    //获取
    public static int getMaxIdle(){
        return 10;
    }

    //获取
    public static int getMaxWaitMillis(){
        return 5000;
    }

    //获取超时时间(Redis默认是2000毫秒)
    public static int getTimeout(){
        return 30000;
    }

    //获取重试次数
    public static int getRetryNum(){
        return 3;
    }

}
