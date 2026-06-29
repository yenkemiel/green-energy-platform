package com.kemiel.greenenergy.module.procurement.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 採購預設方案回應 DTO
 */
@Getter
@Builder
public class ProcurementPresetResponse {

    private Long id;
    private String label;
    private Integer quantity;
    private Integer isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

