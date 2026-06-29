package com.kemiel.greenenergy.module.procurement.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 採購預設方案，對應 procurement_presets 資料表
 */
@Data
public class ProcurementPreset {

    private Long id;
    private String label;
    private Integer quantity;
    private Integer isActive;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer isDeleted;
}
