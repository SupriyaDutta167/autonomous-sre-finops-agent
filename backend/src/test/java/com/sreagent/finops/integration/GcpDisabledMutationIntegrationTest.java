package com.sreagent.finops.integration;

import com.sreagent.finops.execution.*;
import com.sreagent.finops.model.*;
import com.sreagent.finops.safety.*;
import com.sreagent.finops.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GcpDisabledMutationIntegrationTest {

    private GcpClient mockGcpClient;
    private IncidentOrchestrator orchestrator;
    private AuditLogService auditLogService;
    private VerificationService verificationService;

    @BeforeEach
    void setUp() {
        mockGcpClient = mock(GcpClient.class);
        GcpComputeService gcpComputeService = new GcpComputeService("test-project", "test-zone", false, mockGcpClient);
        
        SreReasoningEngine reasoningEngine = mock(SreReasoningEngine.class);
        SreAction action = new SreAction(ActionType.STOP_VM, "target-vm", "Save money", "Idle", 0.95, Severity.LOW, 150.0, false);
        when(reasoningEngine.analyzeAlert(any())).thenReturn(action);

        ActionValidator actionValidator = new ActionValidator();
        SafetyPolicy safetyPolicy = new SafetyPolicy();
        PolicyEngine policyEngine = new PolicyEngine(actionValidator, safetyPolicy);
        
        verificationService = mock(VerificationService.class);
        FinOpsService finOpsService = new FinOpsService();
        auditLogService = mock(AuditLogService.class);
        InfrastructureStateProvider stateProvider = mock(InfrastructureStateProvider.class);
        when(stateProvider.getVmState(any())).thenReturn(new VmState("target-vm", "RUNNING", 1));

        orchestrator = new IncidentOrchestrator(
                reasoningEngine, actionValidator, policyEngine, 
                gcpComputeService, verificationService, finOpsService, 
                auditLogService, stateProvider
        );
    }

    @Test
    void testDisabledMutationDoesNotReportSuccessAndRecordsZeroSavings() throws Exception {
        SystemAlert alert = new SystemAlert("target-vm", 5.0, 50.0, 100.0, Instant.now(), "dev", "RUNNING");
        
        OrchestrationResult result = orchestrator.processAlert(alert);
        
        // 1. GCP mutation disabled prevents SDK mutation call
        verify(mockGcpClient, never()).stopInstance(anyString(), anyString(), anyString());
        
        // 2. Disabled mutation is NOT reported as successful execution
        ExecutionResult executionResult = result.executionResult();
        assertNotNull(executionResult);
        assertFalse(executionResult.success());
        assertEquals("GCP mutation disabled. No infrastructure change performed.", executionResult.message());
        
        // 3. No verification success can be inferred from a disabled mutation (verification skipped because execution failed)
        verify(verificationService, never()).verify(any(), any(), any());
        assertNull(result.verificationResult());
        
        // 4 & 5. No positive FinOps savings are produced, Audit reflects no mutation
        ArgumentCaptor<IncidentAuditLog> logCaptor = ArgumentCaptor.forClass(IncidentAuditLog.class);
        verify(auditLogService).recordIncident(logCaptor.capture());
        
        IncidentAuditLog log = logCaptor.getValue();
        assertEquals(0.0, log.estimatedSavings());
        assertEquals(IncidentStatus.FAILED, log.status());
        assertFalse(log.executionResult().success());
    }
}

