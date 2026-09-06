package com.sreagent.finops.execution;

import com.sreagent.finops.model.ActionType;
import com.sreagent.finops.model.Severity;
import com.sreagent.finops.model.SreAction;
import com.sreagent.finops.service.GcpAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DelegatingInfrastructureExecutorTest {

    private GcpAuthenticationService authService;
    private SimulationExecutor simExecutor;
    private GcpComputeService gcpExecutor;
    private DelegatingInfrastructureExecutor delegator;
    private SreAction action;

    @BeforeEach
    void setUp() {
        authService = mock(GcpAuthenticationService.class);
        simExecutor = mock(SimulationExecutor.class);
        gcpExecutor = mock(GcpComputeService.class);
        delegator = new DelegatingInfrastructureExecutor(simExecutor, gcpExecutor, authService);
        action = new SreAction(ActionType.START_VM, "target", "Reason", "Desc", 0.95, Severity.LOW, 0.0, false);
    }

    @Test
    void testDelegatesToSimulation() {
        when(authService.getRuntimeMode()).thenReturn("SIMULATION");
        when(simExecutor.execute(action)).thenReturn(new ExecutionResult(true, action.action(), action.target(), "Simulated", Instant.now(), "RUNNING"));
        
        ExecutionResult result = delegator.execute(action);
        
        assertEquals("Simulated", result.message());
        verify(simExecutor, times(1)).execute(action);
        verify(gcpExecutor, never()).execute(any());
    }

    @Test
    void testDelegatesToGcp() {
        when(authService.getRuntimeMode()).thenReturn("GCP");
        when(gcpExecutor.execute(action)).thenReturn(new ExecutionResult(true, action.action(), action.target(), "GCP executed", Instant.now(), "RUNNING"));
        
        ExecutionResult result = delegator.execute(action);
        
        assertEquals("GCP executed", result.message());
        verify(gcpExecutor, times(1)).execute(action);
        verify(simExecutor, never()).execute(any());
    }
}
