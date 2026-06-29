package com.kemiel.greenenergy.module.procurement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 完成採購請求 DTO
 */
@Getter
public class CompleteProcurementRequest {

    @NotNull(message = "實際完成日期不可為空")
    private LocalDate completedDate;
}
