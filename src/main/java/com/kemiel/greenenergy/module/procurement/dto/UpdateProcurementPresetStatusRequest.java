package com.kemiel.greenenergy.module.procurement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 更新採購預設方案狀態請求 DTO
 */
@Getter
public class UpdateProcurementPresetStatusRequest {

    @NotNull(message = "啟用狀態不可為空")
    private Boolean isActive;
}

