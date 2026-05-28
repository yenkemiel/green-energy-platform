package com.kemiel.greenenergy.module.solar.controller;

import com.kemiel.greenenergy.common.response.ApiResponse;
import com.kemiel.greenenergy.common.util.SecurityUtils;
import com.kemiel.greenenergy.module.solar.dto.CreateSolarMonthlyRecordRequest;
import com.kemiel.greenenergy.module.solar.dto.SolarMonthlyRecordResponse;
import com.kemiel.greenenergy.module.solar.dto.UpdateSolarMonthlyRecordRequest;
import com.kemiel.greenenergy.module.solar.service.SolarMonthlyRecordService;
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
public class SolarMonthlyRecordController {

    private final SolarMonthlyRecordService solarMonthlyRecordService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<SolarMonthlyRecordResponse>>> listRecords(
            @PathVariable Long id,
            @RequestParam Integer year) {
        return ResponseEntity.ok(ApiResponse.success(
                solarMonthlyRecordService.listRecords(id, year)));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
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
    public ResponseEntity<ApiResponse<SolarMonthlyRecordResponse>> updateRecord(
            @PathVariable Long id,
            @PathVariable Long recordId,
            @RequestBody @Valid UpdateSolarMonthlyRecordRequest request) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(
                solarMonthlyRecordService.updateRecord(id, recordId, request, operatorId)));
    }
}

