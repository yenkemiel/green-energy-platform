package com.kemiel.greenenergy.module.solar.controller;

import com.kemiel.greenenergy.common.response.ApiResponse;
import com.kemiel.greenenergy.common.util.SecurityUtils;
import com.kemiel.greenenergy.module.solar.dto.CreateSolarMonthlyRecordRequest;
import com.kemiel.greenenergy.module.solar.dto.SolarMonthlyRecordResponse;
import com.kemiel.greenenergy.module.solar.dto.UpdateSolarMonthlyRecordRequest;
import com.kemiel.greenenergy.module.solar.service.SolarMonthlyRecordService;
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
 * 太陽能設備月發電紀錄 Controller
 */
@RestController
@RequestMapping("/api/v1/solar-devices/{id}/monthly-records")
@RequiredArgsConstructor
@Tag(name = "Solar Monthly Records", description = "太陽能設備月發電紀錄模組")
public class SolarMonthlyRecordController {

    private final SolarMonthlyRecordService solarMonthlyRecordService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Operation(summary = "[FR-017] 查詢月發電紀錄")
    public ResponseEntity<ApiResponse<List<SolarMonthlyRecordResponse>>> listRecords(
            @PathVariable Long id,
            @RequestParam Integer year) {
        return ResponseEntity.ok(ApiResponse.success(
                solarMonthlyRecordService.listRecords(id, year)));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "[FR-018] 新增月發電紀錄")
    public ResponseEntity<ApiResponse<SolarMonthlyRecordResponse>> createRecord(
            @PathVariable Long id,
            @RequestBody @Valid CreateSolarMonthlyRecordRequest request) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        solarMonthlyRecordService.createRecord(id, request, operatorId)));
    }

    @PutMapping("/{recordId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "[FR-019] 修改月發電紀錄")
    public ResponseEntity<ApiResponse<SolarMonthlyRecordResponse>> updateRecord(
            @PathVariable Long id,
            @PathVariable Long recordId,
            @RequestBody @Valid UpdateSolarMonthlyRecordRequest request) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                solarMonthlyRecordService.updateRecord(id, recordId, request, operatorId)));
    }
}

