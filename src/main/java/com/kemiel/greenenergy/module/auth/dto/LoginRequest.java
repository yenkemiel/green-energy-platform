package com.kemiel.greenenergy.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 登入請求 DTO。
 */
@Getter
public class LoginRequest {
    @NotBlank(message = "帳號不可為空")
    private String username;
    @NotBlank(message = "密碼不可為空")
    private String password;
}
