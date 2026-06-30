package com.kemiel.greenenergy.module.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 待處理事項回應 DTO
 */
@Getter
@Builder
public class PendingItemsResponse {

    private Integer activeContracts;
    private BigDecimal monthlyGuaranteedKwh;
    private Integer inProgressProcurements;
    private List<String> missingDataItems;
    private List<ExpiringContractItem> expiringContracts;
}
