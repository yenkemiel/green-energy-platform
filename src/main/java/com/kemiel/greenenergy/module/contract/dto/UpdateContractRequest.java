package com.kemiel.greenenergy.module.contract.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 修改合約的請求格式（費率不可修改）。
 */
@Getter
public class UpdateContractRequest {
    @NotBlank(message = "供電業者名稱不可為空")
    private String supplierName;

    @NotNull(message = "每月供電度數不可為空")
    @DecimalMin(value = "0.0", inclusive = false, message = "每月供電度數必須大於 0")
    private BigDecimal monthlySupplyKwh;

    @NotNull(message = "合約開始日期不可為空")
    private LocalDate startDate;

    @NotNull(message = "合約結束日期不可為空")
    private LocalDate endDate;

    private String notes;
}
