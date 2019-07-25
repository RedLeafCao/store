package com.can.store.shopping.commons.kiss.helper.session;

import com.can.store.shopping.commons.kizz.lib.utils.Func;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 2019.07.25
 */
public class UserSession {
    private Map<String,Object> info;

    /**
     * 获取用户信息
     * @param key
     * @return
     */
    public Object getValue(String key){
        if(null == key){
            return null;
        }
        if(key.equals("id") || key.equals("user_id")){
            key = "user_id";
        }
        return info.get("user_id");
    }

    /**
     * 获取操作人UID（即通过本人操作实名验证得到的UID）
     * @return
     */
    public Long getUserId(){
        Long l = Func.toLongDefault(info.get("user_id"),0);
        return l;
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
        if (null == this.info) {
            return;
        }

        // 具体实现
    }

    public static Long fetchUserID(){
        try {
            HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpSession session = request.getSession();
            Long u = (Long)session.getAttribute("user_id");
            return u;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static UserSession getInstance(){
        Long user_id = UserSession.fetchUserID();
        if(null == user_id){
            UserSession userSession = new UserSession();
            userSession.info = new HashMap<>();
            return userSession;
        }
        return getInstance(user_id);
    }

    public static UserSession getInstance(Long user_id){
        UserSession us = new UserSession();
        us.info = new HashMap<>();
        us.info.put("user_id",user_id);
        return us;
    }
}
