package com.sreagent.finops.model;

import java.time.Instant;

import com.sreagent.finops.execution.ExecutionResult;
import com.sreagent.finops.service.VerificationResult;

public record IncidentAuditLog(
    String incidentId,
    String targetInstance,
    SystemAlert alert,
    SreAction action,
    PolicyDecision policyDecision,
    IncidentStatus status,
    String reason,
    Instant timestamp,
    double estimatedSavings,
    ExecutionResult executionResult,
    VerificationResult verificationResult
) {}
