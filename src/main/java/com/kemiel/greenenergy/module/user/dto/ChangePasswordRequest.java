package com.kemiel.greenenergy.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChangePasswordRequest {

    @NotBlank(message = "新密碼不可為空")
    private String newPassword;
}
