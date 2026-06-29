package com.kemiel.greenenergy.module.greenenergy.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 綠電來源明細項目 DTO
 */
@Getter
@Builder
public class GreenSummarySourceItem {

    private String type;
    private String label;
    private BigDecimal kwh;
    private String contractType;
    private String supplyType;
}
