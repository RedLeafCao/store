package com.can.store.shopping.commons.mybat.model;

/**
 * 用户基本信息，获取修改用户基本信息，
 * 2019.08.13
 */
public class UserInfos {
    private Integer id = null;
    private Long userId;
    private String password = null;
    private String nickName;
    private Long phone;
    private String icon;
    private Integer gender;
    private Long birthday;
    private Long createTime = null;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gener) {
        this.gender = gener;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
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
        final StringBuilder sb = new StringBuilder("UserInfos{");
        sb.append("id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", password='").append(password).append('\'');
        sb.append(", nickName='").append(nickName).append('\'');
        sb.append(", phone=").append(phone);
        sb.append(", icon='").append(icon).append('\'');
        sb.append(", gener=").append(gender);
        sb.append(", birthday=").append(birthday);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastTime=").append(lastTime);
        sb.append('}');
        return sb.toString();
    }
}
