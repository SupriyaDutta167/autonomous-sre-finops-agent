package com.sreagent.finops.model;

public record SreAction(
    ActionType action,
    String target,
    String reason,
    String rootCause,
    double confidence,
    Severity severity,
    double estimatedSavings,
    boolean requiresApproval
) {}
