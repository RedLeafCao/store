package com.can.store.shopping.commons.mybat.service;

import com.can.store.shopping.commons.mybat.model.User;

import java.util.List;

/**
 * 用户登录注册，密码修改service层
 * 2019.08.08
 */
public interface UserService {
    // 添加新用户
    int addUser(User user);
    // 查询用户账号密码
    User selectUser(Long userId);
    // 修改用户密码
    int updateUser(User user);
    // 删除用户
    int deleteUser(Long userId);
    // 查询所有用户
    List<User> selectAllUser(int pageNum,int pageSize);
}
