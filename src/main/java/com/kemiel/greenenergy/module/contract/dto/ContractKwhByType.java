package com.kemiel.greenenergy.module.contract.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 合約依類型彙整 kWh 中間承載 DTO
 */
@Data
public class ContractKwhByType {
    private String contractType;
    private BigDecimal kwh;
}
