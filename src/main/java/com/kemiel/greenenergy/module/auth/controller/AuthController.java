package com.kemiel.greenenergy.module.auth.controller;

import com.kemiel.greenenergy.common.response.ApiResponse;
import com.kemiel.greenenergy.module.auth.dto.LoginRequest;
import com.kemiel.greenenergy.module.auth.dto.LoginResponse;
import com.kemiel.greenenergy.module.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 認證控制器，處理登入與登出請求。
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "認證", description = "登入、登出")
public class AuthController {

    private final AuthService authService;

    /**
     * 使用者登入，驗證帳號密碼並回傳 JWT Token。
     */
    @PostMapping("/login")
    @Operation(summary = "[FR-001] 使用者登入")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody @Valid LoginRequest request
            ){
        return ResponseEntity.ok(ApiResponse.success(authService.login(request)));
    }

    /**
     * 使用者登出，第一階段 stateless，由前端清除 Token。
     */
    @PostMapping("/logout")
    @Operation(summary = "[FR-002] 使用者登出")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success());
    }
}
