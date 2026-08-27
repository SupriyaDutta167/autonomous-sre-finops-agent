package com.sreagent.finops.service;

import com.sreagent.finops.model.*;
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

    private GeminiSreService geminiSreService;
    private IncidentOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        geminiSreService = mock(GeminiSreService.class);
        ActionValidator actionValidator = new ActionValidator();
        SafetyPolicy safetyPolicy = new SafetyPolicy();
        PolicyEngine policyEngine = new PolicyEngine(actionValidator, safetyPolicy);
        
        orchestrator = new IncidentOrchestrator(geminiSreService, actionValidator, policyEngine);
    }

    private SystemAlert createAlert(String target) {
        return new SystemAlert(target, 95.0, 50.0, 1000.0, Instant.now(), "prod", "RUNNING");
    }

    @Test
    void testSuccessfulGeminiToActionConversionAndApproval() {
        SystemAlert alert = createAlert("dev-web-01");
        SreAction action = new SreAction(ActionType.RESTART_VM, "dev-web-01", "High CPU", "Spike", 0.95, Severity.MEDIUM, 0.0, false);
        
        when(geminiSreService.analyzeAlert(any())).thenReturn(action);
        
        OrchestrationResult result = orchestrator.processAlert(alert);
        
        assertEquals(IncidentStatus.APPROVED, result.finalStatus());
        assertTrue(result.decision().allowed());
        assertEquals(ActionType.RESTART_VM, result.action().action());
    }

    @Test
    void testGeminiFailure() {
        SystemAlert alert = createAlert("dev-web-01");
        when(geminiSreService.analyzeAlert(any())).thenThrow(new RuntimeException("Gemini failed"));
        
        OrchestrationResult result = orchestrator.processAlert(alert);
        
        assertEquals(IncidentStatus.FAILED, result.finalStatus());
        assertNull(result.action());
        assertNull(result.decision());
    }

    @Test
    void testBlockedActionReturnsBlocked() {
        SystemAlert alert = createAlert("prod-db-01");
        SreAction action = new SreAction(ActionType.STOP_VM, "prod-db-01", "Cost savings", "Idle", 0.95, Severity.LOW, 50.0, false);
        
        when(geminiSreService.analyzeAlert(any())).thenReturn(action);
        
        OrchestrationResult result = orchestrator.processAlert(alert);
        
        assertEquals(IncidentStatus.BLOCKED, result.finalStatus());
        assertFalse(result.decision().allowed());
        assertEquals("Production database resources are protected from autonomous shutdown.", result.decision().reason());
    }

    @Test
    void testNoActionFlow() {
        SystemAlert alert = createAlert("prod-web-01");
        SreAction action = new SreAction(ActionType.NO_ACTION, "prod-web-01", "Normal", "None", 0.95, Severity.LOW, 0.0, false);
        
        when(geminiSreService.analyzeAlert(any())).thenReturn(action);
        
        OrchestrationResult result = orchestrator.processAlert(alert);
        
        assertEquals(IncidentStatus.APPROVED, result.finalStatus());
        assertTrue(result.decision().allowed());
    }

    @Test
    void testMalformedGeminiOutputBlockedByValidator() {
        SystemAlert alert = createAlert("dev-web-01");
        SreAction action = new SreAction(ActionType.RESTART_VM, "", "Missing target", "Unknown", 0.95, Severity.LOW, 0.0, false);
        
        when(geminiSreService.analyzeAlert(any())).thenReturn(action);
        
        OrchestrationResult result = orchestrator.processAlert(alert);
        
        assertEquals(IncidentStatus.BLOCKED, result.finalStatus());
        assertFalse(result.decision().allowed());
        assertTrue(result.decision().reason().contains("Validation failed"));
    }
}
