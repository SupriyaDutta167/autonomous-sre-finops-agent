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
    private final com.sreagent.finops.service.AuditLogService auditLogService;

    public DashboardController(SimulationExecutor simulationExecutor, com.sreagent.finops.service.AuditLogService auditLogService) {
        this.simulationExecutor = simulationExecutor;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "healthy"));
    }

    @GetMapping("/simulation/vms")
    public ResponseEntity<Collection<SimulatedVm>> getSimulatedVms() {
        return ResponseEntity.ok(simulationExecutor.getAllVms());
    }

    @GetMapping("/incidents")
    public ResponseEntity<Collection<com.sreagent.finops.model.IncidentAuditLog>> getIncidents() {
        return ResponseEntity.ok(auditLogService.getAllIncidents());
    }
}
