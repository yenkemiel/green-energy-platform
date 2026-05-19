package com.kemiel.greenenergy.module.user.entity;

import com.kemiel.greenenergy.common.enums.RoleType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 使用者帳號實體，對應 users 資料表。
 */
@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String displayName;
    private RoleType role;
    private Boolean isActive;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
