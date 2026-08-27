package com.sreagent.finops.service;

import com.sreagent.finops.model.SystemAlert;
import com.sreagent.finops.model.SreAction;
import com.sreagent.finops.model.PolicyDecision;
import com.sreagent.finops.model.IncidentStatus;

import com.sreagent.finops.execution.ExecutionResult;
import com.sreagent.finops.service.FinOpsResult;
import com.sreagent.finops.service.VerificationResult;

public record OrchestrationResult(
    SystemAlert alert,
    SreAction action,
    PolicyDecision decision,
    IncidentStatus finalStatus,
    ExecutionResult executionResult,
    VerificationResult verificationResult,
    FinOpsResult finOpsResult
) {}
