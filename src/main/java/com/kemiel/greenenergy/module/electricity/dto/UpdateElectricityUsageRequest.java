package com.kemiel.greenenergy.module.electricity.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 修改每月用電量請求 DTO
 */
@Getter
public class UpdateElectricityUsageRequest {

    @NotNull(message = "用電量不可為空")
    @DecimalMin(value = "0.0", inclusive = false, message = "用電量必須大於 0")
    private BigDecimal usageKwh;
}
