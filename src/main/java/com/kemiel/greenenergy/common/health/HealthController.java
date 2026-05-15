package com.kemiel.greenenergy.common.health;

import com.kemiel.greenenergy.common.response.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 健康檢查端點，供 GCP Cloud Run 確認服務存活狀態。
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    /**
     * 回傳服務存活狀態與當前時間戳。
     *
     * @return 健康檢查結果
     */
    @GetMapping
    public ResponseEntity<ApiResponse<HealthResponse>> health() {
        HealthResponse data = HealthResponse.builder()
                .status("UP")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

}
