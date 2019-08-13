package com.can.store.shopping.commons.mybat.service;

import com.can.store.shopping.commons.mybat.model.UserInfo;

/**
 * 用户基本信息获取修改
 * 2019.08.13
 */
public interface UserInfoService {
    UserInfo selectByUserId(Long userId);
    int updateByUserId(UserInfo userInfo);
    int updateLastTime(Long userId,Long lastTime);
}
