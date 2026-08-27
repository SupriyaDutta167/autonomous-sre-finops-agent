package com.sreagent.finops.execution;

import com.sreagent.finops.model.ActionType;
import java.time.Instant;

public record ExecutionResult(
        boolean success,
        ActionType action,
        String target,
        String message,
        Instant timestamp,
        String resultingState
) {
}
