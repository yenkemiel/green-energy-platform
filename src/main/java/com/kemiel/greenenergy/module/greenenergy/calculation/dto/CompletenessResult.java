package com.kemiel.greenenergy.module.greenenergy.calculation.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 月度資料完整度計算層中間承載 DTO
 */
@Data
@Builder
public class CompletenessResult {

    private int year;
    private int month;
    private boolean solarFilled;
    private boolean usageFilled;
    private int completeness;
    private boolean canLock;
}
