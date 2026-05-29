package com.kemiel.greenenergy.module.contract.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合約實體，對應 contracts 資料表。
 */
@Data
public class Contract {
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
    private Long terminatedBy;
    private String notes;
    private Long locationId;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer isDeleted;
}
