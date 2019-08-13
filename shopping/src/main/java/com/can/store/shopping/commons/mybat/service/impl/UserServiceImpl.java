package com.can.store.shopping.commons.mybat.service.impl;

import com.github.pagehelper.PageHelper;
import com.can.store.shopping.commons.mybat.mapper.UserMapper;
import com.can.store.shopping.commons.mybat.model.User;
import com.can.store.shopping.commons.mybat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户登录注册，密码修改服务实现层
 * 2019.8.8
 */
@Service(value = "userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public int addUser(User user) {
        return userMapper.insert(user);
    }

    @Override
    public User selectUser(Long userId) {
        return userMapper.selectByUserId(userId);
    }

    @Override
    public int updateUser(User user) {
        return userMapper.updateByUserId(user);
    }

    @Override
    public int deleteUser(Long userId) {
        return userMapper.deleteByUserId(userId);
    }

    @Override
    public List<User> selectAllUser(int pageNum,int pageSize) {
        // 将参数传给以下方法实现数据物理分页
        PageHelper.startPage(pageNum,pageSize);
        return userMapper.selectAllUser();
    }
}
