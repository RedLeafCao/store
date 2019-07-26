package com.can.store.shopping.commons.kiss.helper.session;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 用户session
 *
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class Session {
    protected static String sessionClassName;
    protected Object sessionIns;

    /**
     * 使用私有无参构造方法，避免外部通过new创建实例
     */
    private Session() {
    }


    public static Session getInstance() {
        if (null == sessionClassName) {
            return null;
        }

        if (sessionClassName.equals("AdminSession")) {
            Session session = new Session();
            session.sessionIns = AdminSession.getInstance();
            return session;
        }

        return null;
    }

    public static void setSessionClass(String classname) {
        sessionClassName = classname;
    }

    public Object getSession() {
        return sessionIns;
    }

    //多子系统的时候使用
//    public static boolean isCmsService(){
//        return null!=sessionClassName && sessionClassName.equals("CmsSession");
//    }

    public static boolean isMchService() {
        return null != sessionClassName && sessionClassName.equals("AdminSession");
    }

    public boolean isMerchantUser() {
        return sessionIns instanceof AdminSession;
    }

    public Object getValue(String key) {
        AdminSession ins = (AdminSession) sessionIns;
        return ins.getValue(key);
    }

    public Integer getValueInteger(String key) {
        return Integer.parseInt(key);
    }

    /**
     * 整数的类型更大可能是Integer，(Long)getValue(key)可能会报错。
     *
     * @param key
     * @return
     */
    public Long getValueLong(String key) {
        return Long.parseLong(key);
    }

    public String getValueString(String key) {
        Object v = getValue(key);
        if (null != v) {
            return v.toString();
        }
        return null;
    }

    public static void set(String key, Object value) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        session.setAttribute(key, value);
    }

    public static String getSessionID(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        return session.getId();
    }
}