package com.can.store.shopping.commons.kizz.db.mysql;


import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class MysqlConnection {
    private static Logger logger = LoggerFactory.getLogger(MysqlConnection.class);

    private static Map<String, Connection> ConnectionPool;

    /**
     * 连接创建的时间
     */
    private static Map<String, Long> ConnectionTimes;

    private static int ConnectionCount;
    private static int ConnectionMax;

    /**
     * 实例创建时间距离当前时间超过closeableWaitTimeout之后，可将实例移到cloesable*
     */
    private static int closeableWaitTimeout;

    /**
     * 可以重用的数据库实例
     */
    private static Map<String, Long> ReusableConnections;

    /**
     * java的类实例传递的都是引用，hash对应数据库实例的方式在多线程程序会产生问题。
     * 所以改为hash映射到数据库配置，get实例的时候new一个对象
     */
    private static Map<String, String[]> hashConfig;
    private static String defaultConfigHash;

    private static Map<String, Connection> closableConnection;
    private static Map<String, Long> closableTime;
    private static long lastCloseAt = 0;

    /**
     * 关闭的时间
     */
    private static int handleTimeout = 20;

    public static void init() {
        ConnectionMax = 500;
        ConnectionCount = 0;
        ConnectionTimes = new TreeMap<>();
        hashConfig = new HashMap<>();
        ConnectionPool = new HashMap<>();
        ReusableConnections = new HashMap<>();

        closableConnection = new HashMap<>();
        closableTime = new HashMap<>();

        closeableWaitTimeout = 3600 * 1000 - 50000;
    }

    /**
     * 设置数据库wait_timeout参数的数值。单位：秒
     * <p>
     * 这个设置会影响到连接资源关闭
     *
     * @param MysqlWaitTimeout
     */
    public static void setWaitTimeout(int MysqlWaitTimeout) {
        closeableWaitTimeout = MysqlWaitTimeout * 1000 - 50000;
    }

    public static String create(String driver, String host, String user, String pass, String suffix) {
        String config_hash = Makehash(driver, host, user, pass, suffix);
        if (null == defaultConfigHash) {
            defaultConfigHash = config_hash;
        }
        if (null != hashConfig.get(config_hash)) {
            return GetReuse(config_hash);
        }

        String[] config = new String[4];
        config[0] = host;
        config[1] = user;
        config[2] = pass;
        config[3] = driver;
        hashConfig.put(config_hash, config);

        return GetReuse(config_hash);
    }

    public static Connection getConnection(String connection_hash) {
        return GetConnectionFunc(connection_hash);
    }

    /**
     * 使用完成之后，归还实例对象。重用实例以提高效能
     *
     * @param connection_hash
     */
    public static void returnResource(String connection_hash) {
        //System.out.println("MysqlConnection::returnResource>> "+connection_hash);
        if (null == ConnectionPool.get(connection_hash)) {
            //System.out.println("MysqlConnection::returnResource>> ConnectionPool.get("+connection_hash+") is null");
            return;
        }

        ReusableConnections.put(connection_hash, 1l);
    }

    protected static void CloseWaitTimeout() {
        if (0 == closableTime.size()) {
            return;
        }

        long now = (new Date()).getTime();
        if (0 != lastCloseAt && now - handleTimeout * 1000 < lastCloseAt) {
            return;
        }
        lastCloseAt = now;

        ArrayList hashList = new ArrayList();
        for (String hash : closableTime.keySet()) {
            long addedTime = closableTime.get(hash);
            Long t = ConnectionTimes.get(hash);
            if (null == t || now - handleTimeout * 1000 < addedTime || t > now - closeableWaitTimeout) {
                // 加入不足handleTimeout秒的，且创建的时间距离当前时间不到closeableWaitTimeout秒的，不关闭
                continue;
            }

            hashList.add(hash);
        }

        for (int i = 0; i < hashList.size(); i++) {
            String hash = (String) hashList.get(i);
            Connection db_t = closableConnection.get(hash);
            if (null != db_t) {
                try {
                    db_t.close();

                    if (!db_t.getAutoCommit()) {
                        db_t.rollback();
                    }
                    db_t = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                db_t = null;
            }
            closableConnection.remove(hash);
            closableTime.remove(hash);
        }
    }

    protected static void RemoveClosedHash() {
        if (0 == ConnectionPool.size()) {
            return;
        }

        long now = (new Date()).getTime();
        if (0 != lastCloseAt && now - handleTimeout * 1000 < lastCloseAt) {
            return;
        }

        ArrayList closedHash = new ArrayList();

        for (String hash_t : ConnectionTimes.keySet()) {
            Connection db_t = ConnectionPool.get(hash_t);
            try {
                if (null == db_t || db_t.isClosed()) {
                    // 已关闭的
                    closedHash.add(hash_t);
                    //System.out.println("RemoveClosedHash>>> "+hash_t+" is closed");
                } else {
                    long t = ConnectionTimes.get(hash_t);
                    if (t + closeableWaitTimeout < now) {
                        // 闲置超时的
                        closedHash.add(hash_t);
                        //System.out.println("RemoveClosedHash>>> "+hash_t+" wait timeout "+t+", "+now);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (0 == closedHash.size()) {
            return;
        }

        for (int ii = 0; ii < closedHash.size(); ii++) {
            String hash_t = (String) closedHash.get(ii);
            closableConnection.put(hash_t, ConnectionPool.get(hash_t));
            closableTime.put(hash_t, now);

            ConnectionPool.remove(hash_t);
            ReusableConnections.remove(hash_t);
            ConnectionTimes.remove(hash_t);
        }
    }

    /**
     * 重用超时实例的方式获取实例。应该注意数据库wait_timeout的设定
     *
     * @param config_hash
     * @return
     */
    protected static String GetReuse(String config_hash) {
        //System.out.println("MysqlConnection::GetReuse:ConnectionsCount="+ConnectionCount);

        RemoveClosedHash();
        CloseWaitTimeout();

        // 找到超过10秒未使用的连接实例，放入可重用数组
        long now = (new Date()).getTime();
        for (String key : ConnectionTimes.keySet()) {
            if (now - ConnectionTimes.get(key) > 10000) {
                ReusableConnections.put(key, now);
                //System.out.println("MysqlConnection::getReuse>>> reuse more than 10 seconds: "+key);
            }
        }

        if (ReusableConnections.size() > 0) {
            //释放事务锁
//            for(String firstHash:ReusableConnections.keySet()) {
//                Connection con=getConnection(firstHash);
//                try {
//                    if (!con.getAutoCommit()) {
//                        con.rollback();
//                        logger.error("数据库连接有事务未释放");
//                    }
//                }
//                catch (Exception e){
//                    logger.error("自动释放事务锁异常");
//                    e.printStackTrace();
//                }
//            }

            String hash_t = null;
            for (String firstHash : ReusableConnections.keySet()) {
                hash_t = firstHash;
                break;
            }

            Connection db = getConnection(hash_t);
            if (null != db) {
                //System.out.println("MysqlConnection::GetReuse:ReusableConnections>>> "+hash_t);
                return hash_t;
            }
        }

        if (ConnectionMax <= ConnectionCount) {
            //TODO 预警
        }

        //System.out.println("MysqlConnection::GetReuse:new instance");
        String[] config = hashConfig.get(config_hash);
        String connection_hash = Makehash(config[3], config[0], config[1], config[2], "" + now);
        Connection conn = genConnection(config[3], config[0], config[1], config[2]);
        ConnectionPool.put(connection_hash, conn);
        ConnectionTimes.put(connection_hash, (new Date()).getTime());
        ConnectionCount++;
        return connection_hash;
    }

    /**
     * 超时释放，建立新连接的方式获取实例。各个实例都是独立的，隔离较重用的方式更干净
     *
     * @param config_hash
     * @return
     */
    protected static Connection GetFree(String config_hash) {
        ArrayList clearHash = new ArrayList();

        //System.out.println("MysqlConnection::ConnectionCount:"+ConnectionCount);
        long now = (new Date()).getTime();
        if (ConnectionCount >= ConnectionMax) {
            for (String key : ConnectionTimes.keySet()) {
                if (now - ConnectionTimes.get(key) > 10000) {
                    clearHash.add(key);
                }
            }
        } else if (ConnectionCount > ConnectionMax / 2) {
            // 超过10秒的连接，释放
            for (String key : ConnectionTimes.keySet()) {
                if (now - ConnectionTimes.get(key) > 10000) {
                    clearHash.add(key);
                }
            }
        }

        if (clearHash.size() > 0) {
            for (int k = 0; k < clearHash.size(); k++) {
                String key = (String) clearHash.get(k);
                try {
                    Connection db = ConnectionPool.get(key);
                    if (null != db) {
                        //System.out.println("MysqlConnection::db free call.");
                        db.close();
                        db = null;
                    }
                    ConnectionPool.remove(key);
                    ConnectionTimes.remove(k);
                    ConnectionCount--;
                    //System.out.println("MysqlConnection:disconnect:"+ConnectionCount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        String[] config = hashConfig.get(config_hash);
        String connection_hash = Makehash(config[3], config[0], config[1], config[2], "" + now);
        Connection conn = genConnection(config[3], config[0], config[1], config[2]);
        ConnectionPool.put(connection_hash, conn);
        ConnectionTimes.put(connection_hash, (new Date()).getTime());
        ConnectionCount++;

        return conn;
    }

    protected static Connection genConnection(String driver, String host, String user, String password) {
        try {
            Class.forName(driver);
            try {
                Connection conn = DriverManager.getConnection(host, user, password);
                return conn;
            } catch (SQLException e) {
                System.out.println("连接Mysql失败");
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Mysql驱动未找到");
            e.printStackTrace();
        }

        return null;
    }

    protected static String Makehash(String driver, String host, String user, String pass, String suffix) {
        if (host.equals("") || user.equals("") || pass.equals("")) {
            return "";
        }
        if (!suffix.equals("")) {
            suffix = "-" + suffix;
        }
        return DigestUtils.md5Hex(driver + host + user + pass) + suffix;
    }


    protected static Connection GetConnectionFunc(String connection_hash) {
        Connection db = ConnectionPool.get(connection_hash);
        try {
            if (null != db && !db.isClosed()) {
                return db;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
