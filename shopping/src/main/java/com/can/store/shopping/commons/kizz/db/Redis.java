package com.can.store.shopping.commons.kizz.db;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.HashMap;
import java.util.Map;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class Redis {
    JedisPool jedisPool;

    private static Redis instance = null;

    private String host;

    private int port;

    private String password;

    private static Logger logger = LoggerFactory.getLogger(Redis.class);

    public static Redis getInstance(String host, int port, String password) {
        if (instance == null) {
            logger.debug("连接redis");
            instance = new Redis(host, port, password);
        }
        if (instance.jedisPool.isClosed()) {
            logger.debug("重连redis");
            instance.connect(host, port, password);
        }
        return instance;
        //return new Redis(host, port, password);
    }

    public Redis(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.connect(host, port, password);
    }

    public void connect(String host, int port, String password) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        //config.setMaxActive(100);
        config.setMaxIdle(10);
        //config.setMaxWait(1000);
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        config.setTestWhileIdle(true);
        config.setTimeBetweenEvictionRunsMillis(30000);
        config.setNumTestsPerEvictionRun(10);
        config.setMinEvictableIdleTimeMillis(60000);
        String ip = host;
        int timeOut = 2000;
        String auth = password;

        try {
            jedisPool = new JedisPool(config, ip, port, timeOut, auth);
            this.host = host;
            this.port = port;
            this.password = password;
        } catch (JedisConnectionException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            return;
        }
    }

    public String get(String key) {
        try {
            Jedis jedis = getResource();
            String value = jedis.get(key);
            jedisPool.returnResource(jedis);
            return value;
        } catch (NullPointerException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    private Jedis getResource() {
        try {
            return jedisPool.getResource();
        } catch (JedisConnectionException e) {
            logger.info("重连jedis");
            connect(host, port, password);
            return jedisPool.getResource();
        }
    }

    public String get(String key, int offset, int end) {
        try {
            Jedis jedis = getResource();
            String value = jedis.getrange(key, offset, end);
            jedisPool.returnResource(jedis);
            return value;
        } catch (NullPointerException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public boolean append(String key, String data) {
        try {
            Jedis jedis = getResource();
            jedis.append(key, data);
            jedisPool.returnResource(jedis);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    /**
     * @param key
     * @param content
     * @param expire
     * @return
     */
    public boolean setExpire(String key, String content, int expire) {
        try {
            Jedis jedis = getResource();
            jedis.set(key, content);
            jedis.expire(key, expire);
            jedisPool.returnResource(jedis);
            //jedisPool.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    public boolean set(String key, String content) {
        try {
            Jedis jedis = getResource();
            jedis.set(key, content);
            jedisPool.returnResource(jedis);
            //jedisPool.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    public boolean set(String key, String content, int offset) {
        try {
            Jedis jedis = getResource();
            jedis.setrange(key, offset, content);
            jedisPool.returnResource(jedis);
            //jedisPool.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    public long get_ttl(String key) {
        long millisecond = 0L;
        try (Jedis jedis = getResource()) {
            millisecond = jedis.ttl(key);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        return millisecond;
    }

    public int get_ttl_second(String key) {
        int second = 0;
        try (Jedis jedis = getResource()) {
            second = (int) (jedis.ttl(key) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        return second;
    }

    /**
     * 另key过期
     *
     * @param key
     * @param seconds
     */
    public boolean expire(String key, int seconds) {
        try {
            Jedis jedis = getResource();
            if (null != jedis.get(key)) {
                jedis.expire(key, seconds);
            }
            jedisPool.returnResource(jedis);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    public boolean delete(String... key) {
        try {
            Jedis jedis = jedisPool.getResource();
            jedis.del(key);
            jedisPool.returnResource(jedis);
            //jedisPool.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    /**
     * 以键值对列表的形式保存的数据内容在读取时，可以通过此方法进行
     *
     * @param key       redis键名
     * @param separator 键值对之间的分割符，默认为&
     * @param ksepar    键值之间的分割符，默认为=号
     * @return
     */
    public Map<String, String> getInArray(String key, String separator, String ksepar) {
        String content = get(key);
        if (null == content) {
            return null;
        }

        if (null == separator || 0 == separator.length()) {
            separator = "&";
        }
        if (null == ksepar || 0 == ksepar.length()) {
            ksepar = "=";
        }

        Map<String, String> data = new HashMap<>();
        String[] arr = content.split(separator);
        for (int i = 0; i < arr.length; i++) {
            String[] kv = arr[i].trim().split(ksepar);
            if (2 == kv.length) {
                String k = kv[0].trim();
                String v = kv[1].trim();
                data.put(k, v);
            }
        }

        return data;
    }

    public Map<String, String> getInArray(String key) {
        return getInArray(key, "&", "=");
    }

    @Override
    protected void finalize() {
        //System.out.println("Redis:finalize");
        logger.info("jedis回收");
        if (null != jedisPool) {
            jedisPool.close();
        }
    }

    public void close() {
        return;
    }
}
