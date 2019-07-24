package com.can.store.shopping.commons.kiss.helper.session;

import com.alibaba.fastjson.JSON;
import com.can.store.shopping.commons.kiss.db.DBResource;
import com.can.store.shopping.commons.kizz.db.Redis;
import com.can.store.shopping.commons.kizz.lib.utils.Func;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class AdminSession {
    private String access_token;
    private String data_token;
    private Map<String, Object> Info;


    /**
     * 获取第一人称用户信息，从用户信息到商户号信息都可以获取
     *
     * @param key
     * @return
     */
    public Object getValue(String key) {
        if (key.equals("access_token")) {
            return getAccessToken();
        }

        if (key.equals("admin_uid") || key.equals("id")
                || key.equals("userid") || key.equals("uid") || key.equals("user_id")
        ) {
            key = "id";
        } else if (key.equals("nickname") || key.equals("nick_name")) {
            key = "nickname";
        }
        return Info.get(key);
    }

    /**
     * 获取操作人UID（即通过本人操作实名验证得到的UID）
     *
     * @return
     */
    public long get_myUid() {
        return Func.toLongDefault(Info.get("id"), 0);
    }

    /**
     * 初始化用户信息。通常在登录的时候执行，如果用户信息有更新，也应该执行1次
     *
     * @param x_name
     * @param openid
     * @param admin_uid
     * @return
     */
    public boolean init(String x_name, String openid, Long admin_uid) {
        return false;
    }

    /**
     * 每隔5分钟从数据库重新加载数据，以使用到最新的信息
     */
    public void reloadInfo() {
        if (null == this.Info) {
            return;
        }

        // 具体实现
    }

    public void saveSession() {
        if (null == this.access_token) {
            return;
        }

        this._saveSession(access_token);
    }

    public static String makeAccessToken(String content) {
        content += (new Date()).getTime();
        return Func.md5sum(content);
    }

    /**
     * 每次接口请求时都应该调用更新
     *
     * @param access_token
     */
    public static boolean updateAccessToken(String access_token) {
        if (null == access_token) {
            return false;
        }
        Redis redisClient = DBResource.getRedisResource();
        String data_token = redisClient.get(access_token);
        if (null == data_token) {
            return false;
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        session.setAttribute("access_token", access_token);
        return true;
    }

    protected void _saveSession(String access_token) {
        String content = Func.json_encode(Info);
        Redis redis = DBResource.getRedisResource();
        String data_token = redis.get(access_token);
        if (null != data_token) {
            redis.delete(data_token);
        }
        data_token = AdminSession.makeAccessToken(access_token);
        redis.setExpire(data_token, content, 86400);
        redis.setExpire(access_token, data_token, 86400 - 5);
        //对外的access_token的过期时间应该小于数据的，

        try {
            /**
             * 注解（ThreadLocal原理）
             *     1.在Spring API中提供了一个非常便捷的工具类RequestContextHolder，能够在Controller中获
             *      取request对象和response对象。
             *     2.原理是：
             *      RequestContextListener实现了ServletRequestListener ,在其覆盖的
             *         requestInitialized(ServletRequestEvent requestEvent)方法中,将request最终设置
             *         到了RequestContextHolder中.
             */
            ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = sra.getRequest();
            HttpSession session = request.getSession();
            session.setAttribute("access_token", access_token);
        } catch (Exception e) {

        }
    }

    public String getAccessToken() {
        return access_token;
    }

    public static String fetchAccessToken() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpSession session = request.getSession();
            return (String) session.getAttribute("access_token");
        } catch (Exception e) {

        }
        return "";
    }

    protected static String fetchDataToken(String access_token) {
        Redis redis = DBResource.getRedisResource();
        return redis.get(access_token);
    }

    public static AdminSession getInstance() {
        String accessToken = AdminSession.fetchAccessToken();
        if (StringUtils.isBlank(accessToken)) {
            AdminSession ins = new AdminSession();
            ins.Info = new HashMap<>();
            return ins;
        }
        return getInstance(accessToken);
    }

    /**
     * 获取实例
     *
     * @param access_token
     * @return
     */
    public static AdminSession getInstance(String access_token) {
        AdminSession ins = new AdminSession();
        ins.Info = new HashMap<>();
        String data_token = AdminSession.fetchDataToken(access_token);
        if (null == data_token) {
            return ins;
        }
        Redis redis = DBResource.getRedisResource();
        String sessionInfo = redis.get(data_token);
        if (null == sessionInfo) {
            return ins;
        }

        try {
            Map<String, Object> data = (Map<String, Object>) JSON.parse(sessionInfo);
            ins.Info.putAll(data);
            ins.access_token = access_token;
            ins.data_token = data_token;
            return ins;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ins;
    }

    /**
     * 退出的只能是商户号。微信/支付宝等自动授权的不能退出
     */
    public void logout() {
        if (null == Info || 0 == Info.size()) {
            return;
        }

        Info.remove("uid");
        Info.remove("name");
        this.saveSession();
    }

    public static void destroy(String access_token) {
        Redis redis = DBResource.getRedisResource();
        String data_token = redis.get(access_token);
        if (null != data_token) {
            redis.delete(data_token);
        }
        redis.delete(access_token);
    }
}