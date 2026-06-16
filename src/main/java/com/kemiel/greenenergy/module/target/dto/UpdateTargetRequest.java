package com.kemiel.greenenergy.module.target.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 修改年度目標的請求格式。
 */
@Getter
public class UpdateTargetRequest {

    @NotNull(message = "年度用電量不可為空")
    private BigDecimal annualElectricityKwh;

    @NotNull(message = "RE100 目標比例不可為空")
    @DecimalMin(value = "0.0", message = "RE100 目標比例不可小於 0")
    @DecimalMax(value = "1.0", message = "RE100 目標比例不可大於 1")
    private BigDecimal re100TargetRatio;

    @NotNull(message = "用電成長率不可為空")
    @DecimalMin(value = "0.0", message = "用電成長率不可小於 0")
    @DecimalMax(value = "1.0", message = "用電成長率不可大於 1")
    private BigDecimal growthRate;
}
