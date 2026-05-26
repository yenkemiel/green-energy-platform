package com.kemiel.greenenergy.module.solar.controller;

import com.kemiel.greenenergy.common.response.ApiResponse;
import com.kemiel.greenenergy.common.response.PageResult;
import com.kemiel.greenenergy.common.util.SecurityUtils;
import com.kemiel.greenenergy.module.solar.dto.CreateSolarDeviceRequest;
import com.kemiel.greenenergy.module.solar.dto.SolarDeviceResponse;
import com.kemiel.greenenergy.module.solar.dto.UpdateSolarDeviceStatusRequest;
import com.kemiel.greenenergy.module.solar.service.SolarDeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 太陽能設備管理 Controller
 */
@RestController
@RequestMapping("/api/v1/solar-devices")
@RequiredArgsConstructor
public class SolarDeviceController {

    private final SolarDeviceService solarDeviceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<PageResult<SolarDeviceResponse>>> listDevices(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                solarDeviceService.listDevices(status, page, size)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse<SolarDeviceResponse>> getDeviceById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(solarDeviceService.getDeviceById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<SolarDeviceResponse>> createDevice(
            @RequestBody @Valid CreateSolarDeviceRequest request) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(solarDeviceService.createDevice(request, operatorId)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<Void>> updateDeviceStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateSolarDeviceStatusRequest request) {
        solarDeviceService.updateDeviceStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success());
    }

}
