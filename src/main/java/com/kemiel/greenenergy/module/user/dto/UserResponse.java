package com.kemiel.greenenergy.module.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 使用者查詢回傳 DTO
 */
@Getter
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String displayName;
    private String role;
    private Boolean isActive;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
