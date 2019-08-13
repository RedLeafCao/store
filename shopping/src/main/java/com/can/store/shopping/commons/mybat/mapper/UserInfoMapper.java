package com.can.store.shopping.commons.mybat.mapper;

import com.can.store.shopping.commons.mybat.model.UserInfos;

/**
 * 用户信息查询修改
 * 2019.08.13
 */
public interface UserInfoMapper {
    UserInfos selectByUserId(Long userId);
    int updateByUserId(UserInfos userInfo);
    int updateLastTime(Long userId,Long lastTime);
}
