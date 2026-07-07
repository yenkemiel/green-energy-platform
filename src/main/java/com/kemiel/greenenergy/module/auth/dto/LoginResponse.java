package com.kemiel.greenenergy.module.auth.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 登入成功回傳 DTO
 */
@Getter
@Builder
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String displayName;
    private String role;
}
