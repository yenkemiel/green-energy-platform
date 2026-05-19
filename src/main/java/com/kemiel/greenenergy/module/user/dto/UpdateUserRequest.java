package com.kemiel.greenenergy.module.user.dto;

import com.kemiel.greenenergy.common.enums.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 修改使用者資料請求 DTO
 */
@Getter
public class UpdateUserRequest {

    @NotBlank(message = "顯示名稱不可為空")
    private String displayName;

    @NotNull(message = "角色名稱不可為空")
    private RoleType role;
}
