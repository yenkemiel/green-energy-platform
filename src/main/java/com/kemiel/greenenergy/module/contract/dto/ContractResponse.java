package com.kemiel.greenenergy.module.contract.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合約清單與單筆查詢的回傳格式。
 */
@Getter
@Builder
public class ContractResponse {
    private Long id;
    private String supplierName;
    private String contractType;
    private BigDecimal monthlySupplyKwh;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal ratePerKwh;
    private BigDecimal monthlyCostSnapshot;
    private String status;
    private LocalDateTime terminatedAt;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
