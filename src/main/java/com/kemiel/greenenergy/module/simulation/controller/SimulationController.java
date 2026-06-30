package com.kemiel.greenenergy.module.simulation.controller;

import com.kemiel.greenenergy.common.response.ApiResponse;
import com.kemiel.greenenergy.module.simulation.dto.SimulationRequest;
import com.kemiel.greenenergy.module.simulation.dto.SimulationResponse;
import com.kemiel.greenenergy.module.simulation.service.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RE100 達成模擬 Controller
 */
@RestController
@RequestMapping("/api/v1/simulation")
@RequiredArgsConstructor
@Tag(name = "Simulation", description = "RE100 達成模擬模組")
public class SimulationController {

    private final SimulationService simulationService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "[FR-047] RE100 達成模擬")
    public ResponseEntity<ApiResponse<SimulationResponse>> simulate(
            @Valid @RequestBody SimulationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(simulationService.simulate(request)));
    }
}