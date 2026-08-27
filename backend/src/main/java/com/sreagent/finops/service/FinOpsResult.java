package com.sreagent.finops.service;

public record FinOpsResult(
        double estimatedMonthlySavings,
        String currency,
        String explanation
) {}
