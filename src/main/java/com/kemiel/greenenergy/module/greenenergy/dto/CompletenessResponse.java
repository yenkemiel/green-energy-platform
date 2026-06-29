package com.kemiel.greenenergy.module.greenenergy.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 月度資料完整度查詢回應 DTO
 */
@Getter
@Builder
public class CompletenessResponse {

    private Integer recordYear;
    private Integer recordMonth;
    private Boolean solarFilled;
    private Boolean usageFilled;
    private Integer completeness;
    private Boolean canLock;
}