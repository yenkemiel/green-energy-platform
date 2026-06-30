package com.kemiel.greenenergy.module.greenenergy.calculation.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * RE100 達成模擬來源明細計算層中間承載 DTO
 */
@Data
@Builder
public class SimulationBreakdown {

    private BigDecimal additionalContractKwh;
    private BigDecimal additionalProcurementKwh;
}
