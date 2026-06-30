package com.kemiel.greenenergy.module.procurement.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 採購依供電類型彙整 kWh 中間承載 DTO
 */
@Data
public class ProcurementKwhBySupplyType {
    private String supplyType;
    private BigDecimal kwh;
}