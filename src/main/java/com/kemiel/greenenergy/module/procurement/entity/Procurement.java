package com.kemiel.greenenergy.module.procurement.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 採購記錄，對應 procurements 資料表
 */
@Data
public class Procurement {
    private Long id;
    private String supplierName;
    private Integer quantity;
    private BigDecimal kwhEquivalent;
    private Integer certificateYear;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String status;
    private LocalDate expectedDate;
    private LocalDate completedDate;
    private String purchaseMonth;
    private LocalDate expiryDate;
    private Integer isVoid;
    private String notes;
    private String supplyType;
    private Long locationId;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer isDeleted;
}
