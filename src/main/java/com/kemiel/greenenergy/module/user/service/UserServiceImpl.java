package com.kemiel.greenenergy.module.user.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kemiel.greenenergy.common.enums.RoleType;
import com.kemiel.greenenergy.common.exception.BusinessException;
import com.kemiel.greenenergy.common.exception.ErrorCode;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.user.dto.CreateUserRequest;
import com.kemiel.greenenergy.module.user.dto.UpdateUserRequest;
import com.kemiel.greenenergy.module.user.dto.UpdateUserStatusRequest;
import com.kemiel.greenenergy.module.user.dto.UserResponse;
import com.kemiel.greenenergy.module.user.entity.User;
import com.kemiel.greenenergy.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 使用者管理 Service 實作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 查詢使用者清單（支援分頁與角色篩選）
     */
    @Override
    public PageResult<UserResponse> listUsers(RoleType role, int page, int size) {
        log.info("查詢使用者清單，role={}, page={}, size={}", role, page, size);
        PageHelper.startPage(page + 1, size);
        List<User> users = userMapper.selectList(role);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        List<UserResponse> content = users.stream()
                .map(this::toResponse)
                .toList();
        return PageResult.of(content, pageInfo);
    }

    /**
     * 建立使用者
     *
     * @param operatorId 操作者 userId（存入 created_by）
     */
    @Override
    public UserResponse createUser(CreateUserRequest request, Long operatorId) {
        log.info("建立使用者，username={}, role={}", request.getUsername(), request.getRole());
        if(userMapper.selectByUsername(request.getUsername()) != null) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        User user =new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDisplayName(request.getDisplayName());
        user.setRole(request.getRole());
        user.setIsActive(true);
        user.setCreatedBy(operatorId);
        userMapper.insert(user);
        log.info("使用者建立成功，id={}", user.getId());
        return toResponse(userMapper.selectById(user.getId()));
    }

    /**
     * 修改使用者資料（displayName、role）
     */
    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("修改使用者資料，id={}, role={}", id, request.getRole());
        User user = findUserOrThrow(id);
        user.setDisplayName(request.getDisplayName());
        user.setRole(request.getRole());
        userMapper.updateById(user);
        return toResponse(userMapper.selectById(id));
    }

    /**
     * 修改使用者啟用狀態
     */
    @Override
    public void updateUserStatus(Long id, UpdateUserStatusRequest request) {
        log.info("修改使用者啟用狀態，id={}, isActive={}", id, request.getIsActive());
        findUserOrThrow(id);
        userMapper.updateStatusById(id, request.getIsActive());
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .createdBy(user.getCreatedBy())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private User findUserOrThrow(Long id) {
        User user = userMapper.selectById(id);
        if(user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }
}
