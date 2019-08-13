package com.can.store.shopping.commons.mybat.mapper;

import com.can.store.shopping.commons.mybat.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户登录注册,密码修改Mapper
 * 2019.08.08
 */
@Mapper
public interface UserMapper {
    int deleteByUserId(Long userId);
    User selectByUserId(Long userId);
    int updateByUserId(User user);
    int insert(User user);
    List<User> selectAllUser();
}
