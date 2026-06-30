package com.kemiel.greenenergy.module.greenenergy.controller;

import com.kemiel.greenenergy.common.response.ApiResponse;
import com.kemiel.greenenergy.module.greenenergy.dto.CompletenessResponse;
import com.kemiel.greenenergy.module.greenenergy.dto.MonthlySummaryResponse;
import com.kemiel.greenenergy.module.greenenergy.service.GreenEnergyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 綠電來源彙整 Controller
 */
@RestController
@RequestMapping("/api/v1/green-energy")
@RequiredArgsConstructor
@Tag(name = "Green Energy", description = "綠電來源彙整模組")
public class GreenEnergyController {

    private final GreenEnergyService greenEnergyService;

    @GetMapping("/monthly-summary")
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Operation(summary = "[FR-043] 查詢當月綠電彙整")
    public ResponseEntity<ApiResponse<MonthlySummaryResponse>> getMonthlySummary(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return ResponseEntity.ok(ApiResponse.success(
                greenEnergyService.getMonthlySummary(year, month)));
    }

    @GetMapping("/completeness")
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Operation(summary = "[FR-044] 查詢資料完整度")
    public ResponseEntity<ApiResponse<CompletenessResponse>> getCompleteness(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return ResponseEntity.ok(ApiResponse.success(
                greenEnergyService.getCompleteness(year, month)));
    }
}