package com.can.store.shopping.commons.kizz.http.request;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class UserAgent {
    protected HttpServletRequest request;
    protected String user_agent;

    public UserAgent(HttpServletRequest request) {
        withRequest(request);
    }

    public UserAgent withRequest(HttpServletRequest request) {
        if (null == request) {
            return this;
        }

        this.request = request;
        user_agent = request.getHeader("User-Agent").toLowerCase();
        return this;
    }

    public boolean invalidRequest() {
        if (null != request) {
            return false;
        }

        return true;
    }

    /**
     * 判断 移动端/PC端
     */
    public boolean isMobile() {
        if (invalidRequest()) {
            return false;
        }

        List<String> mobileAgents = Arrays.asList("ipad", "iphone os", "rv:1.2.3.4", "ucweb", "android", "windows ce", "windows mobile");
        for (String sua : mobileAgents) {
            if (user_agent.indexOf(sua) > -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否微信浏览器
     */
    public boolean isWechat() {
        if (invalidRequest()) {
            return false;
        }

        if (user_agent.indexOf("micromessenger") > -1) {
            return true;
        }
        return false;
    }

    public boolean isAlipay() {
        if (invalidRequest()) {
            return false;
        }

        if (user_agent.indexOf("alipay") > -1) {
            return true;
        }

        return false;
    }
}