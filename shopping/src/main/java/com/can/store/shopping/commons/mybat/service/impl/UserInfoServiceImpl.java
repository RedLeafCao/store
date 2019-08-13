package com.can.store.shopping.commons.mybat.service.impl;

import com.can.store.shopping.commons.mybat.mapper.UserInfoMapper;
import com.can.store.shopping.commons.mybat.model.UserInfo;
import com.can.store.shopping.commons.mybat.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户基本信息获取修改实现
 * 2019.08.13
 */
@Service(value = "userInfoService")
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo selectByUserId(Long userId) {
        return userInfoMapper.selectByUserId(userId);
    }

    @Override
    public int updateByUserId(UserInfo userInfo) {
        return userInfoMapper.updateByUserId(userInfo);
    }

    @Override
    public int updateLastTime(Long userId, Long lastTime) {
        return userInfoMapper.updateLastTime(userId,lastTime);
    }
}
