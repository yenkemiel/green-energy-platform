package com.kemiel.greenenergy.module.procurement.dto;

import lombok.Data;

/**
 * 採購憑證庫存 SQL 聚合查詢結果
 */
@Data
public class ProcurementSummaryStats {
    private Integer totalQuantity;
    private Integer expiringWithin30Days;
}
