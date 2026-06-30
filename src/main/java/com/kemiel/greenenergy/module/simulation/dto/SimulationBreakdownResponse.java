package com.kemiel.greenenergy.module.simulation.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * RE100 達成模擬來源明細回應 DTO
 */
@Getter
@Builder
public class SimulationBreakdownResponse {

    private BigDecimal additionalContractKwh;
    private BigDecimal additionalProcurementKwh;
}
