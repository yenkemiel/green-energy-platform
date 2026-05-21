package com.kemiel.greenenergy.module.user.service;

import com.kemiel.greenenergy.common.enums.RoleType;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.user.dto.*;

/**
 * 使用者管理 Service
 */
public interface UserService {
    PageResult<UserResponse> listUsers(RoleType role, int page, int size);
    UserResponse createUser(CreateUserRequest request, Long operatorId);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void updateUserStatus(Long id, UpdateUserStatusRequest request);
    void resetPassword(Long id, ChangePasswordRequest request);
    void changeMyPassword(ChangePasswordRequest request, Long operatorId);
}
