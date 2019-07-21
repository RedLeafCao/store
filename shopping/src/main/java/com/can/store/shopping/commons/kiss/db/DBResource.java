package com.can.store.shopping.commons.kiss.db;

import com.can.store.shopping.commons.kizz.db.mysql.MysqlDB;
import com.kiss.Config;
import com.kizz.db.Redis;
import com.kizz.db.mysql.MysqlConnection;
import com.kizz.db.mysql.MysqlDB;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class DBResource {
    private static Config config;

    public static void init() {
        config = new Config();
        MysqlConnection.init();
    }

    /**
     * 获取全局的redis实例（可用于全局共享数据）
     *
     * @return
     */
    public static Redis getGlobalRedis() {
        return Redis.getInstance(config.getRedisHost(), config.getRedisPort(), config.getRedisPassword());
    }

    /**
     * 获取本机的redis实例
     *
     * @return
     */
    public static Redis getRedisResource() {
        return Redis.getInstance(config.getRedisHost(), config.getRedisPort(), config.getRedisPassword());
    }

    public static MysqlDB get() {
        return new MysqlDB(config.getMysqlDriver(), config.getMysqlUrl(), config.getMysqlUser(), config.getMysqlPassword());
    }

    public static void returnResource(MysqlDB db) {
        db.free();
        db = null;
    }
}