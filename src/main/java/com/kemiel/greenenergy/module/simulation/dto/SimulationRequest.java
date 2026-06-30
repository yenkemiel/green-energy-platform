package com.kemiel.greenenergy.module.simulation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * RE100 達成模擬請求 DTO
 */
@Getter
public class SimulationRequest {

    @NotNull(message = "假設增加合約供電量不可為空")
    @DecimalMin(value = "0.0", inclusive = true, message = "合約供電量不可為負數")
    private BigDecimal additionalContractKwh;

    @NotNull(message = "假設增加採購張數不可為空")
    @Min(value = 0, message = "採購張數不可為負數")
    private Integer additionalProcurementQuantity;
}