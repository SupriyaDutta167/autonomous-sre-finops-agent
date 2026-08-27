package com.sreagent.finops.service;

import com.sreagent.finops.model.SystemAlert;
import com.sreagent.finops.model.SreAction;
import com.sreagent.finops.model.PolicyDecision;
import com.sreagent.finops.model.IncidentStatus;

public record OrchestrationResult(
    SystemAlert alert,
    SreAction action,
    PolicyDecision decision,
    IncidentStatus finalStatus
) {}
