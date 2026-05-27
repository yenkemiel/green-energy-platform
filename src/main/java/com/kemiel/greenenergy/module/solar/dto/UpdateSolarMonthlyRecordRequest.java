package com.kemiel.greenenergy.module.solar.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 修改月發電紀錄 Request DTO
 */
@Getter
public class UpdateSolarMonthlyRecordRequest {

    @NotNull(message = "實際發電量不可為空")
    @DecimalMin(value = "0.0", inclusive = false, message = "實際發電量必須大於 0")
    private BigDecimal actualKwh;
}
