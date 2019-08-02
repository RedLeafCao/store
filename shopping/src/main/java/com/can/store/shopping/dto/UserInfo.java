package com.can.store.shopping.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private Long user_id;
    private String password;
    @ApiModelProperty(value = "昵称")
    private String nickName;
    @ApiModelProperty(value = "手机号")
    private Long phone;
    @ApiModelProperty(value = "头像")
    private String icon;
    @ApiModelProperty(value = "性别")
    private Integer gender;
    @ApiModelProperty(value = "生日")
    private Long birthday;
    @ApiModelProperty(value = "注册时间")
    private Long createTime;
    @ApiModelProperty(value = "上次登录时间")
    private Long LastTime;

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
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

    public void setGender(Integer gender) {
        this.gender = gender;
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
        return LastTime;
    }

    public void setLastTime(Long lastTime) {
        LastTime = lastTime;
    }
}
