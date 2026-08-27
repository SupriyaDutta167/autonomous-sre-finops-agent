package com.sreagent.finops.controller;

import com.sreagent.finops.model.SystemAlert;
import com.sreagent.finops.service.IncidentOrchestrator;
import com.sreagent.finops.service.OrchestrationResult;
import com.sreagent.finops.simulation.IncidentScenario;
import com.sreagent.finops.simulation.TelemetrySimulator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/simulate")
public class SimulationController {

    private final TelemetrySimulator telemetrySimulator;
    private final IncidentOrchestrator incidentOrchestrator;

    public SimulationController(TelemetrySimulator telemetrySimulator, IncidentOrchestrator incidentOrchestrator) {
        this.telemetrySimulator = telemetrySimulator;
        this.incidentOrchestrator = incidentOrchestrator;
    }

    private ResponseEntity<OrchestrationResult> simulateAndProcess(IncidentScenario scenario) {
        SystemAlert alert = telemetrySimulator.generateTelemetry(scenario);
        OrchestrationResult result = incidentOrchestrator.processAlert(alert);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/cpu-spike")
    public ResponseEntity<OrchestrationResult> simulateCpuSpike() {
        return simulateAndProcess(IncidentScenario.CPU_SPIKE);
    }

    @PostMapping("/memory-leak")
    public ResponseEntity<OrchestrationResult> simulateMemoryLeak() {
        return simulateAndProcess(IncidentScenario.MEMORY_LEAK);
    }

    @PostMapping("/traffic-surge")
    public ResponseEntity<OrchestrationResult> simulateTrafficSurge() {
        return simulateAndProcess(IncidentScenario.TRAFFIC_SURGE);
    }

    @PostMapping("/idle-vm")
    public ResponseEntity<OrchestrationResult> simulateIdleVm() {
        return simulateAndProcess(IncidentScenario.IDLE_VM);
    }

    @PostMapping("/unsafe-action")
    public ResponseEntity<OrchestrationResult> simulateUnsafeAction() {
        return simulateAndProcess(IncidentScenario.UNSAFE_ACTION);
    }
}
