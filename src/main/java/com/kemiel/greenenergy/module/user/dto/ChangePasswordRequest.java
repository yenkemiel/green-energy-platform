package com.kemiel.greenenergy.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ChangePasswordRequest {

    @NotBlank(message = "新密碼不可為空")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*]+$", message = "密碼只能包含英文字母、數字與特殊符號")
    private String newPassword;
}
