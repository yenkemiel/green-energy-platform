package com.kemiel.greenenergy.module.user.mapper;

import com.kemiel.greenenergy.common.enums.RoleType;
import com.kemiel.greenenergy.module.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 使用者帳號 Mapper
 */
@Mapper
public interface UserMapper {
    User selectById(Long id);
    User selectByUsername(String username);
    List<User> selectList(@Param("role")RoleType role);
    void insert(User user);
    void updateById(User user);
    void updateStatusById(@Param("id") Long id, @Param("isActive") Boolean isActive);
    void updatePasswordById(@Param("id") Long id, @Param("password") String password);
}
