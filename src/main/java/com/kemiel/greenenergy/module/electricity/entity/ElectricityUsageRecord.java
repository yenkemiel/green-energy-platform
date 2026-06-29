package com.kemiel.greenenergy.module.electricity.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 每月用電量記錄，對應 electricity_usage_records 資料表
 */
@Data
public class ElectricityUsageRecord {

    private Long id;
    private Integer recordYear;
    private Integer recordMonth;
    private BigDecimal usageKwh;
    private String status;
    private LocalDateTime lockedAt;
    private Long lockedBy;
    private Long locationId;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer isDeleted;
}
