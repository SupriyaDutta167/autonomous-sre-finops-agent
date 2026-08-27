package com.sreagent.finops.model;

import java.time.Instant;

public record IncidentAuditLog(
    String incidentId,
    String targetInstance,
    SystemAlert alert,
    SreAction action,
    PolicyDecision policyDecision,
    IncidentStatus status,
    String reason,
    Instant timestamp,
    double estimatedSavings
) {}
