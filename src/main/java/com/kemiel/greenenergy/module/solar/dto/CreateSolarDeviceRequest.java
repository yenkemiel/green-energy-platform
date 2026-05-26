package com.kemiel.greenenergy.module.solar.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 新增太陽能設備請求 DTO
 */
@Getter
public class CreateSolarDeviceRequest {

    @NotBlank(message = "設備名稱不可為空")
    private String deviceName;

    @NotNull(message = "裝置容量不可為空")
    @DecimalMin(value = "0.0", inclusive = false, message = "裝置容量必須大於 0")
    private BigDecimal capacityKw;

    @NotNull(message = "安裝日期不可為空")
    private LocalDate installDate;

    private String location;
}
