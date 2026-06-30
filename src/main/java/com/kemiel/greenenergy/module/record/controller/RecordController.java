package com.kemiel.greenenergy.module.record.controller;

import com.kemiel.greenenergy.common.response.ApiResponse;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.module.record.dto.RecordItemResponse;
import com.kemiel.greenenergy.module.record.service.RecordService;
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
 * 總紀錄頁面 Controller
 */
@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
@Tag(name = "Records", description = "總紀錄頁面模組")
public class RecordController {

    private final RecordService recordService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    @Operation(summary = "[FR-048] 查詢總紀錄")
    public ResponseEntity<ApiResponse<PageResult<RecordItemResponse>>> listRecords(
            @RequestParam(defaultValue = "ALL") String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String supplyType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                recordService.listRecords(type, status, startDate, endDate,
                        supplyType, page, size)));
    }
}