package com.sreagent.finops.service;

import com.sreagent.finops.model.*;
import com.sreagent.finops.execution.*;
import com.sreagent.finops.safety.ActionValidator;
import com.sreagent.finops.safety.PolicyEngine;
import com.sreagent.finops.safety.SafetyPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IncidentOrchestratorTest {

    private SreReasoningEngine sreReasoningEngine;
    private InfrastructureExecutor infrastructureExecutor;
    private IncidentOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        sreReasoningEngine = mock(SreReasoningEngine.class);
        infrastructureExecutor = mock(InfrastructureExecutor.class);
        ActionValidator actionValidator = new ActionValidator();
        SafetyPolicy safetyPolicy = new SafetyPolicy();
        PolicyEngine policyEngine = new PolicyEngine(actionValidator, safetyPolicy);
        
        orchestrator = new IncidentOrchestrator(sreReasoningEngine, actionValidator, policyEngine, infrastructureExecutor);
    }

    private SystemAlert createAlert(String target) {
        return new SystemAlert(target, 95.0, 50.0, 1000.0, Instant.now(), "prod", "RUNNING");
    }

    @Test
    void testSuccessfulGeminiToActionConversionAndApproval() {
        SystemAlert alert = createAlert("dev-web-01");
        SreAction action = new SreAction(ActionType.RESTART_VM, "dev-web-01", "High CPU", "Spike", 0.95, Severity.MEDIUM, 0.0, false);
        
        when(sreReasoningEngine.analyzeAlert(any())).thenReturn(action);
        
        OrchestrationResult result = orchestrator.processAlert(alert);
        
        assertEquals(IncidentStatus.APPROVED, result.finalStatus());
        assertEquals(DecisionStatus.APPROVED, result.decision().status());
        assertEquals(ActionType.RESTART_VM, result.action().action());
    }

    @Test
    void testGeminiFailure() {
        SystemAlert alert = createAlert("dev-web-01");
        when(sreReasoningEngine.analyzeAlert(any())).thenThrow(new RuntimeException("Gemini failed"));
        
        OrchestrationResult result = orchestrator.processAlert(alert);
        
        assertEquals(IncidentStatus.FAILED, result.finalStatus());
        assertNull(result.action());
        assertNull(result.decision());
    }

    @Test
    void testBlockedActionReturnsBlocked() {
        SystemAlert alert = createAlert("prod-db-01");
        SreAction action = new SreAction(ActionType.STOP_VM, "prod-db-01", "Cost savings", "Idle", 0.95, Severity.LOW, 50.0, false);
        
        when(sreReasoningEngine.analyzeAlert(any())).thenReturn(action);
        
        OrchestrationResult result = orchestrator.processAlert(alert);
        
        assertEquals(IncidentStatus.BLOCKED, result.finalStatus());
        assertEquals(DecisionStatus.BLOCKED, result.decision().status());
        assertEquals("Production database resources are protected from autonomous shutdown.", result.decision().reason());
    }

    @Test
    void testRequiresApprovalActionReturnsApprovalRequired() {
        SystemAlert alert = createAlert("prod-web-01");
        SreAction action = new SreAction(ActionType.SCALE_UP, "prod-web-01", "High traffic", "Spike", 0.95, Severity.MEDIUM, 0.0, true);
        
        when(sreReasoningEngine.analyzeAlert(any())).thenReturn(action);
        
        OrchestrationResult result = orchestrator.processAlert(alert);
        
        assertEquals(IncidentStatus.APPROVAL_REQUIRED, result.finalStatus());
        assertEquals(DecisionStatus.REQUIRES_APPROVAL, result.decision().status());
    }

    @Test
    void testNoActionFlow() {
        SystemAlert alert = createAlert("prod-web-01");
        SreAction action = new SreAction(ActionType.NO_ACTION, "prod-web-01", "Normal", "None", 0.95, Severity.LOW, 0.0, false);
        
        when(sreReasoningEngine.analyzeAlert(any())).thenReturn(action);
        
        OrchestrationResult result = orchestrator.processAlert(alert);
        
        assertEquals(IncidentStatus.APPROVED, result.finalStatus());
        assertEquals(DecisionStatus.APPROVED, result.decision().status());
    }

    @Test
    void testMalformedGeminiOutputBlockedByValidator() {
        SystemAlert alert = createAlert("dev-web-01");
        SreAction action = new SreAction(ActionType.RESTART_VM, "", "Missing target", "Unknown", 0.95, Severity.LOW, 0.0, false);
        
        when(sreReasoningEngine.analyzeAlert(any())).thenReturn(action);
        
        OrchestrationResult result = orchestrator.processAlert(alert);
        
        assertEquals(IncidentStatus.BLOCKED, result.finalStatus());
        assertEquals(DecisionStatus.BLOCKED, result.decision().status());
        assertTrue(result.decision().reason().contains("Validation failed"));
    }

    @Test
    void testIncidentOrchestratorWithMockSreReasoningService() {
        MockSreReasoningService mockEngine = new MockSreReasoningService();
        ActionValidator actionValidator = new ActionValidator();
        SafetyPolicy safetyPolicy = new SafetyPolicy();
        PolicyEngine policyEngine = new PolicyEngine(actionValidator, safetyPolicy);
        InfrastructureExecutor mockExecutor = mock(InfrastructureExecutor.class);
        
        IncidentOrchestrator testOrchestrator = new IncidentOrchestrator(mockEngine, actionValidator, policyEngine, mockExecutor);
        
        SystemAlert alert = createAlert("prod-db-01"); // This will trigger STOP_VM from mock
        OrchestrationResult result = testOrchestrator.processAlert(alert);
        
        assertEquals(IncidentStatus.BLOCKED, result.finalStatus());
        assertEquals(DecisionStatus.BLOCKED, result.decision().status());
        assertEquals(ActionType.STOP_VM, result.action().action());
    }

    @Test
    void testApprovedActionReachesExecutor() {
        SystemAlert alert = createAlert("dev-web-01");
        SreAction action = new SreAction(ActionType.RESTART_VM, "dev-web-01", "High CPU", "Spike", 0.95, Severity.MEDIUM, 0.0, false);
        
        when(sreReasoningEngine.analyzeAlert(any())).thenReturn(action);
        when(infrastructureExecutor.execute(any())).thenReturn(new ExecutionResult(true, ActionType.RESTART_VM, "dev-web-01", "Success", Instant.now(), "RUNNING"));
        
        OrchestrationResult result = orchestrator.processAlert(alert);
        
        assertEquals(IncidentStatus.APPROVED, result.finalStatus());
        verify(infrastructureExecutor, times(1)).execute(any());
    }

    @Test
    void testBlockedActionNeverReachesExecutor() {
        SystemAlert alert = createAlert("prod-db-01");
        SreAction action = new SreAction(ActionType.STOP_VM, "prod-db-01", "Cost savings", "Idle", 0.95, Severity.LOW, 50.0, false);
        
        when(sreReasoningEngine.analyzeAlert(any())).thenReturn(action);
        
        OrchestrationResult result = orchestrator.processAlert(alert);
        
        assertEquals(IncidentStatus.BLOCKED, result.finalStatus());
        verify(infrastructureExecutor, never()).execute(any());
    }

    @Test
    void testRequiresApprovalActionNeverReachesExecutor() {
        SystemAlert alert = createAlert("prod-web-01");
        SreAction action = new SreAction(ActionType.SCALE_UP, "prod-web-01", "High traffic", "Spike", 0.95, Severity.MEDIUM, 0.0, true);
        
        when(sreReasoningEngine.analyzeAlert(any())).thenReturn(action);
        
        OrchestrationResult result = orchestrator.processAlert(alert);
        
        assertEquals(IncidentStatus.APPROVAL_REQUIRED, result.finalStatus());
        verify(infrastructureExecutor, never()).execute(any());
    }
}
