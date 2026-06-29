package com.kemiel.greenenergy.module.electricity.controller;

import com.kemiel.greenenergy.common.response.ApiResponse;
import com.kemiel.greenenergy.common.util.SecurityUtils;
import com.kemiel.greenenergy.module.electricity.dto.CreateElectricityUsageRequest;
import com.kemiel.greenenergy.module.electricity.dto.ElectricityUsageResponse;
import com.kemiel.greenenergy.module.electricity.dto.UpdateElectricityUsageRequest;
import com.kemiel.greenenergy.module.electricity.service.ElectricityUsageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 每月用電量登記 Controller
 */
@RestController
@RequestMapping("/api/v1/electricity-usage")
@RequiredArgsConstructor
@Tag(name = "Electricity Usage", description = "每月用電量登記模組")
public class ElectricityUsageController {

    private final ElectricityUsageService electricityUsageService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Operation(summary = "[FR-039] 查詢用電量紀錄")
    public ResponseEntity<ApiResponse<List<ElectricityUsageResponse>>> listByYear(
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(ApiResponse.success(electricityUsageService.listByYear(year)));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "[FR-040] 新增月用電量")
    public ResponseEntity<ApiResponse<ElectricityUsageResponse>> createRecord(
            @Valid @RequestBody CreateElectricityUsageRequest request) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(electricityUsageService.createRecord(request, operatorId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "[FR-041] 修改月用電量")
    public ResponseEntity<ApiResponse<ElectricityUsageResponse>> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody UpdateElectricityUsageRequest request) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                electricityUsageService.updateRecord(id, request, operatorId)));
    }

    @PostMapping("/{id}/lock")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "[FR-042] 鎖定月份")
    public ResponseEntity<ApiResponse<Void>> lockRecord(@PathVariable Long id) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        electricityUsageService.lockRecord(id, operatorId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
