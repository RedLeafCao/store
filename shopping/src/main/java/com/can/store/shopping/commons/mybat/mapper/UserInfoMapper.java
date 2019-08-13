package com.can.store.shopping.commons.mybat.mapper;

import com.can.store.shopping.commons.mybat.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信息查询修改
 * 2019.08.13
 */
@Mapper
public interface UserInfoMapper {
    UserInfo selectByUserId(Long userId);
    int updateByUserId(UserInfo userInfo);
    int updateLastTime(Long userId,Long lastTime);
}
