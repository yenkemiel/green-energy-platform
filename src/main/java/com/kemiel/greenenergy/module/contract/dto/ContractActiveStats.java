package com.kemiel.greenenergy.module.contract.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 履約中合約統計中間承載 DTO
 */
@Data
public class ContractActiveStats {
    private Integer activeContracts;
    private BigDecimal monthlyGuaranteedKwh;
}
