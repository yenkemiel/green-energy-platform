package com.kemiel.greenenergy.module.simulation.service;

import com.kemiel.greenenergy.module.simulation.dto.SimulationRequest;
import com.kemiel.greenenergy.module.simulation.dto.SimulationResponse;

/**
 * RE100 達成模擬 Service 介面
 */
public interface SimulationService {

    SimulationResponse simulate(SimulationRequest request);
}
