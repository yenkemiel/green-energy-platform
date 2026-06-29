package com.kemiel.greenenergy.module.procurement.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 採購憑證庫存總覽回傳格式
 */
@Getter
@Builder
public class ProcurementSummaryResponse {

    private Integer totalQuantity;
    private Integer usedQuantity;
    private Integer availableQuantity;
    private Integer expiringWithin30Days;
}
