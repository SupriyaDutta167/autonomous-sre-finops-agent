package com.sreagent.finops.integration;

import com.sreagent.finops.execution.ExecutionResult;
import com.sreagent.finops.execution.GcpClient;
import com.sreagent.finops.execution.GcpComputeService;
import com.sreagent.finops.model.ActionType;
import com.sreagent.finops.model.Severity;
import com.sreagent.finops.model.SreAction;
import com.sreagent.finops.service.GcpAuthenticationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GcpDisabledMutationIntegrationTest {

    @Test
    public void testMutationDisabled() throws Exception {
        GcpClient mockGcpClient = mock(GcpClient.class);
        GcpAuthenticationService authService = mock(GcpAuthenticationService.class);
        when(authService.isConnected()).thenReturn(true);
        when(authService.getProjectId()).thenReturn("test-project");
        when(authService.getZone()).thenReturn("test-zone");
        when(authService.isMutationsEnabled()).thenReturn(false);

        GcpComputeService service = new GcpComputeService(authService, mockGcpClient);
        
        SreAction action = new SreAction(ActionType.START_VM, "target", "Reason", "Desc", 0.95, Severity.LOW, 0.0, false);
        ExecutionResult result = service.execute(action);
        
        assertFalse(result.success());
    }
}

