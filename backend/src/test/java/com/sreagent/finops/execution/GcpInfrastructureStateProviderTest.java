package com.sreagent.finops.execution;

import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.StatusCode;
import com.sreagent.finops.service.GcpAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GcpInfrastructureStateProviderTest {

    private GcpClient mockGcpClient;
    private GcpAuthenticationService mockAuthService;

    @BeforeEach
    void setUp() {
        mockGcpClient = mock(GcpClient.class);
        mockAuthService = mock(GcpAuthenticationService.class);
        when(mockAuthService.isConnected()).thenReturn(true);
        when(mockAuthService.getProjectId()).thenReturn("test-project");
        when(mockAuthService.getZone()).thenReturn("test-zone");
    }

    @Test
    void missingConfigurationThrowsException() {
        when(mockAuthService.isConnected()).thenReturn(false);
        GcpInfrastructureStateProvider provider = new GcpInfrastructureStateProvider(mockAuthService, mockGcpClient);
        VmState state = provider.getVmState("target-vm");
        assertEquals("UNKNOWN", state.state());
    }

    @Test
    void returnsStateWhenFound() throws Exception {
        GcpInfrastructureStateProvider provider = new GcpInfrastructureStateProvider(mockAuthService, mockGcpClient);
        
        VmState mockState = new VmState("target-vm", "RUNNING", 4);
        when(mockGcpClient.getInstanceState("test-project", "test-zone", "target-vm")).thenReturn(mockState);
        
        VmState result = provider.getVmState("target-vm");
        
        assertEquals("target-vm", result.instanceName());
        assertEquals("RUNNING", result.state());
        assertEquals(4, result.capacity());
    }

    @Test
    void returnsUnknownWhenNull() throws Exception {
        GcpInfrastructureStateProvider provider = new GcpInfrastructureStateProvider(mockAuthService, mockGcpClient);
        
        when(mockGcpClient.getInstanceState("test-project", "test-zone", "target-vm")).thenReturn(null);
        
        VmState result = provider.getVmState("target-vm");
        
        assertEquals("target-vm", result.instanceName());
        assertEquals("UNKNOWN", result.state());
    }

    @Test
    void returnsUnknownOnNotFoundException() throws Exception {
        GcpInfrastructureStateProvider provider = new GcpInfrastructureStateProvider(mockAuthService, mockGcpClient);
        
        NotFoundException nfe = mock(NotFoundException.class);
        when(mockGcpClient.getInstanceState("test-project", "test-zone", "target-vm")).thenThrow(nfe);
        
        VmState result = provider.getVmState("target-vm");
        
        assertEquals("target-vm", result.instanceName());
        assertEquals("UNKNOWN", result.state());
    }

    @Test
    void throwsExecutionExceptionOnOtherErrors() throws Exception {
        GcpInfrastructureStateProvider provider = new GcpInfrastructureStateProvider(mockAuthService, mockGcpClient);
        
        when(mockGcpClient.getInstanceState("test-project", "test-zone", "target-vm"))
                .thenThrow(new RuntimeException("API error"));
        
        GcpExecutionException exception = assertThrows(GcpExecutionException.class, () -> {
            provider.getVmState("target-vm");
        });
        
        assertTrue(exception.getMessage().contains("Failed to read instance state from GCP: API error"));
    }
}

