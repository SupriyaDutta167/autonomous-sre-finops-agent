package com.sreagent.finops.service;

import com.sreagent.finops.execution.ExecutionResult;
import com.sreagent.finops.execution.InfrastructureStateProvider;
import com.sreagent.finops.execution.VmState;
import com.sreagent.finops.model.SreAction;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class VerificationService {

    private final InfrastructureStateProvider stateProvider;

    public VerificationService(InfrastructureStateProvider stateProvider) {
        this.stateProvider = stateProvider;
    }

    public VerificationResult verify(SreAction action, ExecutionResult executionResult, VmState previousState) {
        if (executionResult == null || !executionResult.success()) {
            return new VerificationResult(false, action.action(), action.target(), "Execution was not successful", Instant.now());
        }

        VmState currentState = stateProvider.getVmState(action.target());
        boolean successful = false;
        String message = "";

        switch (action.action()) {
            case START_VM:
            case RESTART_VM:
                successful = "RUNNING".equals(currentState.state());
                message = successful ? "VM is RUNNING" : "VM is not RUNNING";
                break;
            case STOP_VM:
                successful = "STOPPED".equals(currentState.state());
                message = successful ? "VM is STOPPED" : "VM is not STOPPED";
                break;
            case SCALE_UP:
                successful = currentState.capacity() > (previousState != null ? previousState.capacity() : 0);
                message = successful ? "Capacity increased" : "Capacity did not increase";
                break;
            case SCALE_DOWN:
                successful = currentState.capacity() < (previousState != null ? previousState.capacity() : Integer.MAX_VALUE);
                message = successful ? "Capacity decreased" : "Capacity did not decrease";
                break;
            case NO_ACTION:
                successful = previousState == null || (previousState.capacity() == currentState.capacity() && previousState.state().equals(currentState.state()));
                message = successful ? "No infrastructure mutation occurred" : "Unexpected infrastructure mutation";
                break;
            default:
                successful = false;
                message = "Unknown action type for verification";
                break;
        }

        return new VerificationResult(successful, action.action(), action.target(), message, Instant.now());
    }
}
