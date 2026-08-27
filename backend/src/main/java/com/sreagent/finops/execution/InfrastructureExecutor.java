package com.sreagent.finops.execution;

import com.sreagent.finops.model.SreAction;

public interface InfrastructureExecutor {
    ExecutionResult execute(SreAction action);
}
