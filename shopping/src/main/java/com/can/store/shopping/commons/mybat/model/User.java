package com.can.store.shopping.commons.mybat.model;

/**
 * 用户登录注册实体类
 * 2019.08.08
 */

public class User {
    private Integer id = null;
    private Long userId;
    private String password;
    private Long createTime;
    private Long lastTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getLastTime() {
        return lastTime;
    }

    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("userId=").append(userId);
        sb.append(", password='").append(password).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append(", lastTime=").append(lastTime);
        sb.append('}');
        return sb.toString();
    }
}
