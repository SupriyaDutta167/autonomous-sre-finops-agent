package com.sreagent.finops.model;

public record PolicyDecision(
    DecisionStatus status,
    ActionType action,
    String reason
) {}
