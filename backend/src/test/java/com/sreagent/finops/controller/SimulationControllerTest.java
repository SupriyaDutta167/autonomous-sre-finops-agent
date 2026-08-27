package com.sreagent.finops.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sreagent.finops.execution.ExecutionResult;
import com.sreagent.finops.execution.InfrastructureExecutor;
import com.sreagent.finops.model.*;
import com.sreagent.finops.service.IncidentOrchestrator;
import com.sreagent.finops.service.OrchestrationResult;
import com.sreagent.finops.simulation.IncidentScenario;
import com.sreagent.finops.simulation.TelemetrySimulator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SimulationController.class)
class SimulationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TelemetrySimulator telemetrySimulator;

    @MockBean
    private IncidentOrchestrator incidentOrchestrator;

    @Test
    void testSimulateCpuSpike() throws Exception {
        SystemAlert alert = new SystemAlert("dev-web-01", 95.0, 50.0, 1000.0, Instant.now(), "prod", "RUNNING");
        SreAction action = new SreAction(ActionType.SCALE_UP, "dev-web-01", "High CPU", "Spike", 0.95, Severity.MEDIUM, 0.0, false);
        PolicyDecision decision = new PolicyDecision(DecisionStatus.APPROVED, ActionType.SCALE_UP, "Approved by policy");
        ExecutionResult execResult = new ExecutionResult(true, ActionType.SCALE_UP, "dev-web-01", "Success", Instant.now(), "RUNNING");
        com.sreagent.finops.service.VerificationResult verResult = new com.sreagent.finops.service.VerificationResult(true, ActionType.SCALE_UP, "dev-web-01", "OK", Instant.now());
        com.sreagent.finops.service.FinOpsResult finOpsResult = new com.sreagent.finops.service.FinOpsResult(0.0, "USD", "OK");
        OrchestrationResult result = new OrchestrationResult(alert, action, decision, IncidentStatus.RESOLVED, execResult, verResult, finOpsResult);

        when(telemetrySimulator.generateTelemetry(IncidentScenario.CPU_SPIKE)).thenReturn(alert);
        when(incidentOrchestrator.processAlert(alert)).thenReturn(result);

        mockMvc.perform(post("/api/simulate/cpu-spike"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finalStatus").value("RESOLVED"))
                .andExpect(jsonPath("$.executionResult.success").value(true))
                .andExpect(jsonPath("$.verificationResult.successful").value(true));
    }

    @Test
    void testSimulateIdleVm() throws Exception {
        SystemAlert alert = new SystemAlert("dev-batch-01", 5.0, 10.0, 0.0, Instant.now(), "dev", "RUNNING");
        SreAction action = new SreAction(ActionType.STOP_VM, "dev-batch-01", "Idle", "Idle", 0.95, Severity.LOW, 50.0, false);
        PolicyDecision decision = new PolicyDecision(DecisionStatus.APPROVED, ActionType.STOP_VM, "Approved");
        ExecutionResult execResult = new ExecutionResult(true, ActionType.STOP_VM, "dev-batch-01", "Stopped", Instant.now(), "STOPPED");
        com.sreagent.finops.service.VerificationResult verResult = new com.sreagent.finops.service.VerificationResult(true, ActionType.STOP_VM, "dev-batch-01", "OK", Instant.now());
        com.sreagent.finops.service.FinOpsResult finOpsResult = new com.sreagent.finops.service.FinOpsResult(150.0, "USD", "Savings");
        OrchestrationResult result = new OrchestrationResult(alert, action, decision, IncidentStatus.RESOLVED, execResult, verResult, finOpsResult);

        when(telemetrySimulator.generateTelemetry(IncidentScenario.IDLE_VM)).thenReturn(alert);
        when(incidentOrchestrator.processAlert(alert)).thenReturn(result);

        mockMvc.perform(post("/api/simulate/idle-vm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finalStatus").value("RESOLVED"))
                .andExpect(jsonPath("$.finOpsResult.estimatedMonthlySavings").value(150.0));
    }

    @Test
    void testSimulateUnsafeActionReturnsBlocked() throws Exception {
        SystemAlert alert = new SystemAlert("prod-db-01", 5.0, 10.0, 0.0, Instant.now(), "prod", "RUNNING");
        SreAction action = new SreAction(ActionType.STOP_VM, "prod-db-01", "Idle", "Idle", 0.95, Severity.LOW, 50.0, false);
        PolicyDecision decision = new PolicyDecision(DecisionStatus.BLOCKED, ActionType.STOP_VM, "Protected DB");
        OrchestrationResult result = new OrchestrationResult(alert, action, decision, IncidentStatus.BLOCKED, null, null, null);

        when(telemetrySimulator.generateTelemetry(IncidentScenario.UNSAFE_ACTION)).thenReturn(alert);
        when(incidentOrchestrator.processAlert(alert)).thenReturn(result);

        mockMvc.perform(post("/api/simulate/unsafe-action"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.finalStatus").value("BLOCKED"))
                .andExpect(jsonPath("$.executionResult").isEmpty()) // it should be missing or null
                .andExpect(jsonPath("$.verificationResult").isEmpty())
                .andExpect(jsonPath("$.finOpsResult").isEmpty());
    }
}
