package com.sreagent.finops.execution;

public record VmState(
    String instanceName,
    String state,
    int capacity
) {}
