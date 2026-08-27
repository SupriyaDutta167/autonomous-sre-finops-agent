package com.sreagent.finops.service;

import com.sreagent.finops.model.ActionType;
import java.time.Instant;

public record VerificationResult(
        boolean successful,
        ActionType action,
        String target,
        String message,
        Instant timestamp
) {}
