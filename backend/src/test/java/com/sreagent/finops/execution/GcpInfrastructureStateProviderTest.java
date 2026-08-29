package com.sreagent.finops.execution;

import com.google.api.gax.rpc.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GcpInfrastructureStateProviderTest {

    private GcpClient mockGcpClient;

    @BeforeEach
    void setUp() {
        mockGcpClient = mock(GcpClient.class);
    }

    @Test
    void missingConfigurationThrowsException() {
        assertThrows(GcpConfigurationException.class, () -> new GcpInfrastructureStateProvider(null, "zone", mockGcpClient));
        assertThrows(GcpConfigurationException.class, () -> new GcpInfrastructureStateProvider("project", null, mockGcpClient));
    }

    @Test
    void returnsStateWhenInstanceExists() throws Exception {
        VmState expectedState = new VmState("target-vm", "RUNNING", 1);
        when(mockGcpClient.getInstanceState("test-project", "test-zone", "target-vm")).thenReturn(expectedState);

        GcpInfrastructureStateProvider provider = new GcpInfrastructureStateProvider("test-project", "test-zone", mockGcpClient);
        VmState actualState = provider.getVmState("target-vm");

        assertEquals(expectedState, actualState);
    }

    @Test
    void returnsUnknownWhenInstanceNull() throws Exception {
        when(mockGcpClient.getInstanceState("test-project", "test-zone", "target-vm")).thenReturn(null);

        GcpInfrastructureStateProvider provider = new GcpInfrastructureStateProvider("test-project", "test-zone", mockGcpClient);
        VmState actualState = provider.getVmState("target-vm");

        assertEquals("target-vm", actualState.instanceName());
        assertEquals("UNKNOWN", actualState.state());
    }

    @Test
    void returnsUnknownWhenInstanceNotFound() throws Exception {
        NotFoundException notFoundException = mock(NotFoundException.class);
        when(mockGcpClient.getInstanceState("test-project", "test-zone", "target-vm"))
            .thenThrow(notFoundException); // Simulating NotFound

        GcpInfrastructureStateProvider provider = new GcpInfrastructureStateProvider("test-project", "test-zone", mockGcpClient);
        VmState actualState = provider.getVmState("target-vm");

        assertEquals("target-vm", actualState.instanceName());
        assertEquals("UNKNOWN", actualState.state());
    }

    @Test
    void throwsExecutionExceptionOnOtherErrors() throws Exception {
        when(mockGcpClient.getInstanceState("test-project", "test-zone", "target-vm"))
            .thenThrow(new RuntimeException("API error"));

        GcpInfrastructureStateProvider provider = new GcpInfrastructureStateProvider("test-project", "test-zone", mockGcpClient);
        
        GcpExecutionException exception = assertThrows(GcpExecutionException.class, () -> {
            provider.getVmState("target-vm");
        });
        
        assertTrue(exception.getMessage().contains("Failed to read instance state from GCP: API error"));
    }
}
