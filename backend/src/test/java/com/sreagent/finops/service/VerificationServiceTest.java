package com.sreagent.finops.service;

import com.sreagent.finops.execution.ExecutionResult;
import com.sreagent.finops.execution.InfrastructureStateProvider;
import com.sreagent.finops.execution.VmState;
import com.sreagent.finops.model.ActionType;
import com.sreagent.finops.model.Severity;
import com.sreagent.finops.model.SreAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VerificationServiceTest {

    private InfrastructureStateProvider stateProvider;
    private VerificationService verificationService;

    @BeforeEach
    void setUp() {
        stateProvider = mock(InfrastructureStateProvider.class);
        verificationService = new VerificationService(stateProvider);
    }

    private SreAction createAction(ActionType type) {
        return new SreAction(type, "test-vm", "reason", "desc", 0.95, Severity.LOW, 0, false);
    }

    private ExecutionResult successResult(ActionType type) {
        return new ExecutionResult(true, type, "test-vm", "Success", Instant.now(), "MOCK_STATE");
    }

    @Test
    void testSuccessfulStopVmVerification() {
        when(stateProvider.getVmState("test-vm")).thenReturn(new VmState("test-vm", "STOPPED", 1));
        VerificationResult result = verificationService.verify(createAction(ActionType.STOP_VM), successResult(ActionType.STOP_VM), null);
        assertTrue(result.successful());
    }

    @Test
    void testSuccessfulStartVmVerification() {
        when(stateProvider.getVmState("test-vm")).thenReturn(new VmState("test-vm", "RUNNING", 1));
        VerificationResult result = verificationService.verify(createAction(ActionType.START_VM), successResult(ActionType.START_VM), null);
        assertTrue(result.successful());
    }

    @Test
    void testSuccessfulScaleUpVerification() {
        when(stateProvider.getVmState("test-vm")).thenReturn(new VmState("test-vm", "RUNNING", 4));
        VmState previous = new VmState("test-vm", "RUNNING", 2);
        VerificationResult result = verificationService.verify(createAction(ActionType.SCALE_UP), successResult(ActionType.SCALE_UP), previous);
        assertTrue(result.successful());
    }

    @Test
    void testSuccessfulScaleDownVerification() {
        when(stateProvider.getVmState("test-vm")).thenReturn(new VmState("test-vm", "RUNNING", 1));
        VmState previous = new VmState("test-vm", "RUNNING", 2);
        VerificationResult result = verificationService.verify(createAction(ActionType.SCALE_DOWN), successResult(ActionType.SCALE_DOWN), previous);
        assertTrue(result.successful());
    }

    @Test
    void testFailedVerificationWhenActualResultIsUnexpected() {
        when(stateProvider.getVmState("test-vm")).thenReturn(new VmState("test-vm", "RUNNING", 1));
        VerificationResult result = verificationService.verify(createAction(ActionType.STOP_VM), successResult(ActionType.STOP_VM), null);
        assertFalse(result.successful());
    }
}
