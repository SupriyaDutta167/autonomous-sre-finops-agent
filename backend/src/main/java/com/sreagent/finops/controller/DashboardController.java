package com.sreagent.finops.controller;

import com.sreagent.finops.execution.SimulatedVm;
import com.sreagent.finops.execution.SimulationExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final SimulationExecutor simulationExecutor;

    public DashboardController(SimulationExecutor simulationExecutor) {
        this.simulationExecutor = simulationExecutor;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "healthy"));
    }

    @GetMapping("/simulation/vms")
    public ResponseEntity<Collection<SimulatedVm>> getSimulatedVms() {
        return ResponseEntity.ok(simulationExecutor.getAllVms());
    }
}
