package com.Jayce.Redis.Utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 类名称：
 * 类描述：
 * <p>
 * 创建人： KevinHui
 * <p>
 * 创建时间：2018/8/21 10:33
 * <p>
 * 修改人：       修改时间：       修改备注：
 * <p>
 * <p>
 * Copyright (c) 2017 厦门自贸试验区电子口岸有限公司-版权所有
 */
public class JedisUtil {



    /**
     * 私有构造器.
     */
    private JedisUtil() {

    }
    private static Map<String,JedisPool> maps  = new HashMap<String,JedisPool>();


    /**
     * 获取连接池.
     * @return 连接池实例
     */
    public static JedisPool getPool(String ip, int port) {
        String key = ip + ":" + port;
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(10);
        config.setMaxWaitMillis(3000);
        JedisPool pool = new JedisPool(config, ip, port);
        //这里为了提供大多数情况下线程池Map里面已经有对应ip的线程池直接返回，提高效率
        if(maps.containsKey(key)){
            pool = maps.get(key);
            return pool;
        }
//        这里的同步代码块防止多个线程同时产生多个相同的ip线程池
        synchronized (JedisUtil.class) {
            if (!maps.containsKey(key)) {
                try {
                    /**
                     * 如果你遇到 java.net.SocketTimeoutException: Read timed out
                     * exception的异常信息 请尝试在构造JedisPool的时候设置自己的超时值.
                     * JedisPool默认的超时时间是2秒(单位毫秒)
                     */
                    pool = new JedisPool(config, ip, port);
                    maps.put(key, pool);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                pool = maps.get(key);
            }
        }
        return pool;
    }



    /**
     *类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例
     *没有绑定关系，而且只有被调用到时才会装载，从而实现了延迟加载。
     */
    private static class RedisUtilHolder{
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        private static JedisUtil instance = new JedisUtil();
    }

    /**
     *当getInstance方法第一次被调用的时候，它第一次读取
     *RedisUtilHolder.instance，导致RedisUtilHolder类得到初始化；而这个类在装载并被初始化的时候，会初始化它的静
     *态域，从而创建RedisUtil的实例，由于是静态的域，因此只会在虚拟机装载类的时候初始化一次，并由虚拟机来保证它的线程安全性。
     *这个模式的优势在于，getInstance方法并没有被同步，并且只是执行一个域的访问，因此延迟初始化并没有增加任何访问成本。
     */
    public static JedisUtil getInstance() {
        return RedisUtilHolder.instance;
    }

    /**
     * 获取Redis实例.
     * @return Redis工具类实例  把类中的redis连接池重构掉，按这个方法做，然后做成线程安全的
     */
    public static Jedis getJedis(String ip,int port,String password) {
        //初始化 jedis 对象
        Jedis jedis  = null;
        int count =0;

        //初始化 JedisPool 连接池
        JedisPoolConfig config=new JedisPoolConfig(); // 连接池的配置对象
        config.setMaxTotal(100); // 设置最大连接数
        config.setMaxIdle(10); // 设置最大空闲连接数
        JedisPool jedisPool=new JedisPool(config,"127.0.0.1",6379);

        do{
            try{
                jedis = jedisPool.getResource();
                //log.info("get redis master1!");
            } catch (Exception e) {
//                log.error("get redis master1 failed!", e);
                // 销毁对象
                getPool(ip,port).returnBrokenResource(jedis);
            }
            count++;
        }while(jedis==null&&count<RedisConfig.getRetryNum());
        return jedis;
    }

    /**
     * 释放redis实例到连接池.
     * @param jedis redis实例
     */
    public static void closeJedis(Jedis jedis,String ip,int port) {
        if(jedis != null) {
            getPool(ip,port).returnResource(jedis);
        }
    }


}
