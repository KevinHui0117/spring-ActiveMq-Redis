package com.Jayce.Redis.Utils;

import com.Jayce.Redis.Model.User;
import com.sun.org.apache.xpath.internal.SourceTree;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;
import redis.clients.util.Pool;
import redis.clients.util.SafeEncoder;

import java.util.*;

/**
 * Created by Administrator on 2017/3/1.
 */
public class JedisClient {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.auth("admin123");
//        connectTest(jedis);
//        setString(jedis);
//        setObject(jedis);
//        saveString(jedis);
//        saveList(jedis);
//        saveSet(jedis);
//        listAllKey(jedis);
        jedisTrans(jedis);
        jedis.close();

    }


    private static void setString(Jedis jedis) {
        jedis.set("name", "jayce");
        String name = jedis.get("name");
        System.out.println(name);
    }


    private static void setObject(Jedis jedis) {
        User user = new User();
        user.setId(1);
        user.setName("jayce");
        user.setPassword("kong");
        byte[] values = SerializeUtil.serialize(user);
        byte[] names = "user".getBytes();
        jedis.set(names, values);
        byte[] bytes = jedis.get("user".getBytes());
        User userCache = (User) SerializeUtil.unserialize(bytes);
        System.out.println(userCache);
    }

    private static void saveSet(Jedis jedis) {
        String setKey = "set-key";
        jedis.sadd(setKey, "-1", "-1", "2");
        System.out.println("[saveSet]:" + Arrays.toString(jedis.smembers(setKey).toArray()));
    }

    private static void listAllKey(Jedis jedis) {
        Set<String> keys = jedis.keys("*");
        Iterator<String> iterator = keys.iterator();
        System.out.print("[listAllKey]:");
        while (iterator.hasNext()) {
            System.out.print(" " + iterator.next());
        }
        System.out.println();
    }

    private static void saveList(Jedis jedis) {
        String listKey = "save-list";

        jedis.lpush(listKey, "[list1]" + UUID.randomUUID().toString());
        jedis.lpush(listKey, "[list2]" + UUID.randomUUID().toString());
        jedis.lpush(listKey, "[list3]" + UUID.randomUUID().toString());
//        jedis.rpush(listKey, "[list4]" + UUID.randomUUID().toString());
//        jedis.rpush(listKey, "[list5]" + UUID.randomUUID().toString());
//        jedis.rpush(listKey, "[list6]" + UUID.randomUUID().toString());

        System.out.println("[saveList]:" + Arrays.toString(jedis.lrange(listKey, 0, 5).toArray()));
    }

    private static void saveString(Jedis jedis) {
        jedis.set("hello", "tony");
        String value = jedis.get("hello");
        System.out.println("[saveString]:" + value);
    }

    private static void connectTest(Jedis jedis) {
        System.out.println("[connectTest]:" + jedis.ping());
    }

    //我们调用jedis.watch(…)方法来监控key，如果调用后key值发生变化，则整个事务会执行失败。
    //另外，事务中某个操作失败，并不会回滚其他操作。这一点需要注意。还有，我们可以使用discard()方法来取消事务。

    /**
     * 身份证校验接口方案：
     * 1.洪峰期间，每秒30万条数据，配置一主二从，主服务器可以写入数据，从服务器可以读取数据
     * 2.配置哨兵，主服务器宕掉之后，立即把从服务器切换为主服务器
     * 3.本地化校验使用从服务器读取数据，在java层面进行校验
     * 4.按照 RDB 模式设置数据持久化，定时任务将 RDB 的数据放到数据库里面
     * @param jedis
     */
    private static void jedisTrans(Jedis jedis) {
        long start = System.currentTimeMillis();
        Transaction tx = jedis.multi();
        for (int i = 0; i < 100000; i++) {
            tx.set("t" + i, "t" + i);
        }
        List<Object> results = tx.exec();
        long end = System.currentTimeMillis();
        System.out.println("Transaction SET: " + ((end - start)/1000.0) + " seconds"+":Content="+results.toArray().toString());
    }



}


