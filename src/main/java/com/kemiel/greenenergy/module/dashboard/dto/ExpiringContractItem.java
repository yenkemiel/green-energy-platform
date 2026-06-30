package com.kemiel.greenenergy.module.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 即將到期合約項目 DTO
 */
@Getter
@Builder
public class ExpiringContractItem {

    private Long contractId;
    private String supplierName;
    private LocalDate endDate;
    private Long daysUntilExpiry;
}