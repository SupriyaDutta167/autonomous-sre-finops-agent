package com.sreagent.finops.execution;

import com.sreagent.finops.model.ActionType;
import com.sreagent.finops.model.Severity;
import com.sreagent.finops.model.SreAction;
import com.sreagent.finops.service.GcpAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GcpComputeServiceTest {

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

    private SreAction createAction(ActionType type, String target) {
        return new SreAction(type, target, "Reason", "Desc", 0.95, Severity.LOW, 0.0, false);
    }

    @Test
    void implementsInfrastructureExecutor() {
        GcpComputeService service = new GcpComputeService(mockAuthService, mockGcpClient);
        assertTrue(service instanceof InfrastructureExecutor);
    }

    @Test
    void missingConfigurationThrowsException() {
        // Not configured returns false
        when(mockAuthService.isConnected()).thenReturn(false);
        GcpComputeService service = new GcpComputeService(mockAuthService, mockGcpClient);
        ExecutionResult result = service.execute(createAction(ActionType.START_VM, "target-vm"));
        assertFalse(result.success());
        assertEquals("GCP not connected or configured.", result.message());
    }

    @Test
    void mutationGuardDisabledByDefaultAndPreventsApiCall() throws Exception {
        when(mockAuthService.isMutationsEnabled()).thenReturn(false);
        GcpComputeService service = new GcpComputeService(mockAuthService, mockGcpClient);
        ExecutionResult result = service.execute(createAction(ActionType.START_VM, "target-vm"));

        assertFalse(result.success());
        assertEquals("GCP mutation disabled. No infrastructure change performed.", result.message());
        assertEquals("UNKNOWN", result.resultingState());
        verify(mockGcpClient, never()).startInstance(anyString(), anyString(), anyString());
    }

    @Test
    void correctActionMappingStartVm() throws Exception {
        when(mockAuthService.isMutationsEnabled()).thenReturn(true);
        GcpComputeService service = new GcpComputeService(mockAuthService, mockGcpClient);
        ExecutionResult result = service.execute(createAction(ActionType.START_VM, "target-vm"));

        assertTrue(result.success());
        assertEquals("RUNNING", result.resultingState());
        verify(mockGcpClient, times(1)).startInstance("test-project", "test-zone", "target-vm");
    }

    @Test
    void correctActionMappingStopVm() throws Exception {
        when(mockAuthService.isMutationsEnabled()).thenReturn(true);
        GcpComputeService service = new GcpComputeService(mockAuthService, mockGcpClient);
        ExecutionResult result = service.execute(createAction(ActionType.STOP_VM, "target-vm"));

        assertTrue(result.success());
        assertEquals("STOPPED", result.resultingState());
        verify(mockGcpClient, times(1)).stopInstance("test-project", "test-zone", "target-vm");
    }

    @Test
    void correctActionMappingRestartVm() throws Exception {
        when(mockAuthService.isMutationsEnabled()).thenReturn(true);
        GcpComputeService service = new GcpComputeService(mockAuthService, mockGcpClient);
        ExecutionResult result = service.execute(createAction(ActionType.RESTART_VM, "target-vm"));

        assertTrue(result.success());
        assertEquals("RUNNING", result.resultingState());
        verify(mockGcpClient, times(1)).restartInstance("test-project", "test-zone", "target-vm");
    }

    @Test
    void correctErrorMapping() throws Exception {
        when(mockAuthService.isMutationsEnabled()).thenReturn(true);
        GcpComputeService service = new GcpComputeService(mockAuthService, mockGcpClient);
        
        doThrow(new RuntimeException("API error")).when(mockGcpClient).startInstance(anyString(), anyString(), anyString());
        
        GcpExecutionException exception = assertThrows(GcpExecutionException.class, () -> {
            service.execute(createAction(ActionType.START_VM, "target-vm"));
        });
        
        assertTrue(exception.getMessage().contains("Failed to execute GCP action: API error"));
    }
}
