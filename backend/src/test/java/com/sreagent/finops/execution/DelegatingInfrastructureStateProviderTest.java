package com.sreagent.finops.execution;

import com.sreagent.finops.service.GcpAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DelegatingInfrastructureStateProviderTest {

    private GcpAuthenticationService authService;
    private SimulationExecutor simProvider;
    private GcpInfrastructureStateProvider gcpProvider;
    private DelegatingInfrastructureStateProvider delegator;

    @BeforeEach
    void setUp() {
        authService = mock(GcpAuthenticationService.class);
        simProvider = mock(SimulationExecutor.class);
        gcpProvider = mock(GcpInfrastructureStateProvider.class);
        delegator = new DelegatingInfrastructureStateProvider(simProvider, gcpProvider, authService);
    }

    @Test
    void testDelegatesToSimulation() {
        when(authService.getRuntimeMode()).thenReturn("SIMULATION");
        when(simProvider.getVmState("target")).thenReturn(new VmState("target", "RUNNING", 1));
        
        VmState result = delegator.getVmState("target");
        
        assertEquals("RUNNING", result.state());
        verify(simProvider, times(1)).getVmState("target");
        verify(gcpProvider, never()).getVmState(any());
    }

    @Test
    void testDelegatesToGcp() {
        when(authService.getRuntimeMode()).thenReturn("GCP");
        when(gcpProvider.getVmState("target")).thenReturn(new VmState("target", "STOPPED", 2));
        
        VmState result = delegator.getVmState("target");
        
        assertEquals("STOPPED", result.state());
        verify(gcpProvider, times(1)).getVmState("target");
        verify(simProvider, never()).getVmState(any());
    }
}
