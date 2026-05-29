package com.kemiel.greenenergy.module.contract.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 終止合約的請求格式。
 */
@Getter
public class TerminateContractRequest {

    @NotNull(message = "終止時間不可為空")
    private LocalDateTime terminatedAt;
}
