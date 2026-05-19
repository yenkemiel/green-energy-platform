package com.kemiel.greenenergy.module.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 修改使用者狀態請求 DTO
 */
@Getter
public class UpdateUserStatusRequest {

    @NotNull(message = "啟用狀態不可為空")
    private Boolean isActive;
}
