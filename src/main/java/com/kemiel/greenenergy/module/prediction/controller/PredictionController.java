package com.kemiel.greenenergy.module.prediction.controller;

import com.kemiel.greenenergy.common.response.ApiResponse;
import com.kemiel.greenenergy.module.prediction.dto.PredictionResponse;
import com.kemiel.greenenergy.module.prediction.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 缺口預測 Controller
 */
@RestController
@RequestMapping("/api/v1/prediction")
@RequiredArgsConstructor
@Tag(name = "Prediction", description = "缺口預測模組")
public class PredictionController {

    private final PredictionService predictionService;

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "[FR-046] 年底達成率預測")
    public ResponseEntity<ApiResponse<PredictionResponse>> getPrediction(
            @RequestParam(required = false) Integer year) {
        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(ApiResponse.success(predictionService.getPrediction(targetYear)));
    }
}