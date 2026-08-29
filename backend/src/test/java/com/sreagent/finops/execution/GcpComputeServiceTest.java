package com.sreagent.finops.execution;

import com.sreagent.finops.model.ActionType;
import com.sreagent.finops.model.Severity;
import com.sreagent.finops.model.SreAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GcpComputeServiceTest {

    private GcpClient mockGcpClient;

    @BeforeEach
    void setUp() {
        mockGcpClient = mock(GcpClient.class);
    }

    private SreAction createAction(ActionType type, String target) {
        return new SreAction(type, target, "Reason", "Desc", 0.95, Severity.LOW, 0.0, false);
    }

    @Test
    void implementsInfrastructureExecutor() {
        GcpComputeService service = new GcpComputeService("test-project", "test-zone", false, mockGcpClient);
        assertTrue(service instanceof InfrastructureExecutor);
    }

    @Test
    void missingConfigurationThrowsException() {
        assertThrows(GcpConfigurationException.class, () -> new GcpComputeService(null, "test-zone", false, mockGcpClient));
        assertThrows(GcpConfigurationException.class, () -> new GcpComputeService("", "test-zone", false, mockGcpClient));
        assertThrows(GcpConfigurationException.class, () -> new GcpComputeService("test-project", null, false, mockGcpClient));
        assertThrows(GcpConfigurationException.class, () -> new GcpComputeService("test-project", "", false, mockGcpClient));
    }

    @Test
    void mutationGuardDisabledByDefaultAndPreventsApiCall() throws Exception {
        GcpComputeService service = new GcpComputeService("test-project", "test-zone", false, mockGcpClient);
        ExecutionResult result = service.execute(createAction(ActionType.START_VM, "target-vm"));

        assertFalse(result.success());
        assertEquals("GCP mutation disabled. No infrastructure change performed.", result.message());
        assertEquals("UNKNOWN", result.resultingState());
        verify(mockGcpClient, never()).startInstance(anyString(), anyString(), anyString());
    }

    @Test
    void correctActionMappingStartVm() throws Exception {
        GcpComputeService service = new GcpComputeService("test-project", "test-zone", true, mockGcpClient);
        ExecutionResult result = service.execute(createAction(ActionType.START_VM, "target-vm"));

        assertTrue(result.success());
        assertEquals("RUNNING", result.resultingState());
        verify(mockGcpClient, times(1)).startInstance("test-project", "test-zone", "target-vm");
    }

    @Test
    void correctActionMappingStopVm() throws Exception {
        GcpComputeService service = new GcpComputeService("test-project", "test-zone", true, mockGcpClient);
        ExecutionResult result = service.execute(createAction(ActionType.STOP_VM, "target-vm"));

        assertTrue(result.success());
        assertEquals("STOPPED", result.resultingState());
        verify(mockGcpClient, times(1)).stopInstance("test-project", "test-zone", "target-vm");
    }

    @Test
    void correctActionMappingRestartVm() throws Exception {
        GcpComputeService service = new GcpComputeService("test-project", "test-zone", true, mockGcpClient);
        ExecutionResult result = service.execute(createAction(ActionType.RESTART_VM, "target-vm"));

        assertTrue(result.success());
        assertEquals("RUNNING", result.resultingState());
        verify(mockGcpClient, times(1)).restartInstance("test-project", "test-zone", "target-vm");
    }

    @Test
    void correctErrorMapping() throws Exception {
        GcpComputeService service = new GcpComputeService("test-project", "test-zone", true, mockGcpClient);
        
        doThrow(new RuntimeException("API error")).when(mockGcpClient).startInstance(anyString(), anyString(), anyString());
        
        GcpExecutionException exception = assertThrows(GcpExecutionException.class, () -> {
            service.execute(createAction(ActionType.START_VM, "target-vm"));
        });
        
        assertTrue(exception.getMessage().contains("Failed to execute GCP action: API error"));
    }
}
