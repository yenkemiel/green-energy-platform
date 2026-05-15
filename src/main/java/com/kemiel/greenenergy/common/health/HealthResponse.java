package com.kemiel.greenenergy.common.health;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 健康檢查回傳資料。
 */
@Getter
@Builder
public class HealthResponse {
    private final String status;
    private final LocalDateTime timestamp;
}
