package com.sreagent.finops.model;

public record PolicyDecision(
    boolean allowed,
    ActionType action,
    String reason
) {}
