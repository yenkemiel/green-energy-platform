package com.kemiel.greenenergy.module.user.controller;

import com.kemiel.greenenergy.common.enums.RoleType;
import com.kemiel.greenenergy.common.response.ApiResponse;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.common.util.SecurityUtils;
import com.kemiel.greenenergy.module.user.dto.*;
import com.kemiel.greenenergy.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 使用者管理 Controller，提供帳號 CRUD、狀態管理與密碼管理
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "帳號管理（限 ADMIN）")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[FR-003] 查詢使用者清單")
    public ResponseEntity<ApiResponse<PageResult<UserResponse>>> listUsers(
            @RequestParam(required = false) RoleType role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResult<UserResponse> result = userService.listUsers(role, page, size);
        return ResponseEntity.ok(ApiResponse.success(result));

    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[FR-004] 建立使用者")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        UserResponse response =userService.createUser(request, operatorId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success( response));

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[FR-005] 修改使用者資料")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        UserResponse response = userService.updateUser(id, request, operatorId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[FR-006] 修改使用者啟用狀態")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        userService.updateUserStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[FR-007] 重設使用者密碼")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.resetPassword(id, request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PutMapping("/me/password")
    @Operation(summary = "[FR-008] 修改自己密碼")
    public ResponseEntity<ApiResponse<Void>> changeMyPassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        userService.changeMyPassword(request, operatorId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}