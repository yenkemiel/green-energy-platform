package com.kemiel.greenenergy.module.user.dto;

import com.kemiel.greenenergy.common.enums.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

/**
 * 建立使用者請求 DTO
 */
@Getter
public class CreateUserRequest {

    @NotBlank(message = "帳號不可為空")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "帳號只能包含英文字母與數字")
    private String username;

    @NotBlank(message = "密碼不可為空")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*]+$", message = "密碼只能包含英文字母、數字與特殊符號")
    private String password;

    @NotBlank(message = "顯示名稱不可為空")
    private String displayName;

    @NotNull(message = "角色不可為空")
    private RoleType role;
}
