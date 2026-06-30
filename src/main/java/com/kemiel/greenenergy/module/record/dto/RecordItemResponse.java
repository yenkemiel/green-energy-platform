package com.kemiel.greenenergy.module.record.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 總紀錄頁面查詢回應 DTO，合約與採購共用
 */
@Getter
@Builder
public class RecordItemResponse {

    private String recordType;
    private Long id;
    private String title;
    private String status;
    private String contractType;
    private BigDecimal monthlySupplyKwh;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer quantity;
    private BigDecimal kwhEquivalent;
    private LocalDate completedDate;
    private BigDecimal totalAmount;
    private String supplyType;
    private LocalDateTime eventDate;
    private Long createdBy;
}