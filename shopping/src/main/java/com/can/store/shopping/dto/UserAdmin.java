package com.can.store.shopping.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

/**
 * 登录注册参数
 * 2019.08.02
 */
public class UserAdmin implements Serializable {
    @ApiModelProperty(value = "用户id")
    @NotEmpty(message = "用户账号不能为空")
    private Long user_id;
    @ApiModelProperty(value = "用户密码",required = true)
    @NotEmpty(message = "用户密码不能少于6位")
    private String password;
    @ApiModelProperty("用户昵称")
    private String nick_name;
    @ApiModelProperty("手机号")
    private Long phone;
    @ApiModelProperty("头像")
    private String icon;
    @ApiModelProperty("性别")
    private Integer gender;
    @ApiModelProperty("生日")
    private Long birthday;

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

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
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
}
