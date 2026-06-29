package com.kemiel.greenenergy.module.electricity.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 新增每月用電量請求 DTO
 */
@Getter
public class CreateElectricityUsageRequest {

    @NotNull(message = "記錄年份不可為空")
    @Min(value = 2000, message = "記錄年份不合理")
    private Integer recordYear;

    @NotNull(message = "記錄月份不可為空")
    @Min(value = 1, message = "月份最小為 1")
    @Max(value = 12, message = "月份最大為 12")
    private Integer recordMonth;

    @NotNull(message = "用電量不可為空")
    @DecimalMin(value = "0.0", inclusive = false, message = "用電量必須大於 0")
    private BigDecimal usageKwh;
}
