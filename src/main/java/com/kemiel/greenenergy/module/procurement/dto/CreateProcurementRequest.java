package com.kemiel.greenenergy.module.procurement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 建立採購草稿請求 DTO。
 */
@Getter
public class CreateProcurementRequest {

    @NotBlank(message = "供應商名稱不可為空")
    private String supplierName;

    @NotNull(message = "採購張數不可為空")
    @Min(value = 1, message = "採購張數最少為 1")
    private Integer quantity;

    @NotNull(message = "憑證年度不可為空")
    private Integer certificateYear;

    @NotNull(message = "每張單價不可為空")
    @DecimalMin(value = "0.0", inclusive = false,message = "每張單價必須大於 0")
    private BigDecimal unitPrice;

    private LocalDate expectedDate;
    private String notes;

    @NotBlank(message = "供電類型不可為空")
    private String supplyType;
}
