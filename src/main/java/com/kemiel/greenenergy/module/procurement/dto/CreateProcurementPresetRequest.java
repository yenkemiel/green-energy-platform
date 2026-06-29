package com.kemiel.greenenergy.module.procurement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 建立採購預設方案請求 DTO
 */
@Getter
public class CreateProcurementPresetRequest {

    @NotBlank(message = "方案名稱不可為空")
    private String label;

    @NotNull(message = "採購張數不可為空")
    @Min(value = 1, message = "採購張數最少為 1")
    private Integer quantity;
}
