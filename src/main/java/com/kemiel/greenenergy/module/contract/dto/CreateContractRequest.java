package com.kemiel.greenenergy.module.contract.dto;

import com.kemiel.greenenergy.common.enums.ContractType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 建立合約的請求格式
 */
@Getter
public class CreateContractRequest {

    @NotBlank(message = "供電業者名稱不可為空")
    private String supplierName;

    @NotNull(message = "合約類型不可為空")
    private ContractType contractType;

    @NotNull(message = "每月供電度數不可為空")
    @DecimalMin(value = "0.0", inclusive = false, message = "每月供電度數必須大於 0")
    private BigDecimal monthlySupplyKwh;

    @NotNull(message = "合約開始日期不可為空")
    private LocalDate startDate;

    @NotNull(message = "合約結束日期不可為空")
    private LocalDate endDate;

    @NotNull(message = "每度費率不可為空")
    @DecimalMin(value = "0.0", inclusive = false, message = "每度費率必須大於 0")
    private BigDecimal ratePerKwh;
    private String notes;
}
