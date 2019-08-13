package com.can.store.shopping.commons.mybat.service.impl;

import com.can.store.shopping.commons.mybat.mapper.UserInfoMapper;
import com.can.store.shopping.commons.mybat.model.UserInfos;
import com.can.store.shopping.commons.mybat.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户基本信息获取修改实现
 * 2019.08.13
 */
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfos selectByUserId(Long userId) {
        return userInfoMapper.selectByUserId(userId);
    }

    @Override
    public int updateByUserId(UserInfos userInfo) {
        return userInfoMapper.updateByUserId(userInfo);
    }

    @Override
    public int updateLastTime(Long userId, Long lastTime) {
        return userInfoMapper.updateLastTime(userId,lastTime);
    }
}
