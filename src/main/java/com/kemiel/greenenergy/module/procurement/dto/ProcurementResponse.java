package com.kemiel.greenenergy.module.procurement.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 採購記錄回應 DTO。
 */
@Getter
@Builder
public class ProcurementResponse {
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
