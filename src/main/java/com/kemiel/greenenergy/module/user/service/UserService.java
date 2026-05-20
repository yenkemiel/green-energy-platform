package com.kemiel.greenenergy.module.user.service;

import com.kemiel.greenenergy.common.enums.RoleType;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.user.dto.CreateUserRequest;
import com.kemiel.greenenergy.module.user.dto.UpdateUserRequest;
import com.kemiel.greenenergy.module.user.dto.UpdateUserStatusRequest;
import com.kemiel.greenenergy.module.user.dto.UserResponse;

/**
 * 使用者管理 Service
 */
public interface UserService {
    PageResult<UserResponse> listUsers(RoleType role, int page, int size);
    UserResponse createUser(CreateUserRequest request, Long operatorId);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void updateUserStatus(Long id, UpdateUserStatusRequest request);

}
