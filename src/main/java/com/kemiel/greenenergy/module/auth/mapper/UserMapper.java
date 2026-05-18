package com.kemiel.greenenergy.module.auth.mapper;

import com.kemiel.greenenergy.module.auth.entity.User;
import org.apache.ibatis.annotations.Mapper;


/**
 * 使用者帳號 Mapper 介面。
 */
@Mapper
public interface UserMapper {
    User selectById(Long id);
    User selectByUsername(String username);
    void insert(User user);
    void updateById(User user);
}
