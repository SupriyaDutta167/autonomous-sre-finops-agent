package com.sreagent.finops.model;

import java.time.Instant;

public record SystemAlert(
    String instanceName,
    double cpuUtilization,
    double memoryUtilization,
    double requestRate,
    Instant timestamp,
    String environment,
    String instanceState
) {}
