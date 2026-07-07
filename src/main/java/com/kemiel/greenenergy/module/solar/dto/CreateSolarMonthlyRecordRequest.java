package com.kemiel.greenenergy.module.solar.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 新增月發電紀錄 Request DTO
 */
@Getter
public class CreateSolarMonthlyRecordRequest {

    @NotNull(message = "年份不可為空")
    @Min(value = 2000, message = "記錄年份不合理")
    private Integer recordYear;

    @NotNull(message = "月份不可為空")
    @Min(value = 1, message = "月份最小為 1")
    @Max(value = 12, message = "月份最大為 12")
    private Integer recordMonth;

    @NotNull(message = "實際發電量不可為空")
    @DecimalMin(value = "0.0", inclusive = false, message = "實際發電量必須大於0")
    private BigDecimal actualKwh;
}
