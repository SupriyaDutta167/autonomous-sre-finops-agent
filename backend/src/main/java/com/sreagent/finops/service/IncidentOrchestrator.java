package com.sreagent.finops.service;

import com.sreagent.finops.model.SystemAlert;
import com.sreagent.finops.model.SreAction;
import com.sreagent.finops.model.PolicyDecision;
import com.sreagent.finops.model.IncidentStatus;
import com.sreagent.finops.safety.ActionValidator;
import com.sreagent.finops.safety.PolicyEngine;
import org.springframework.stereotype.Service;

@Service
public class IncidentOrchestrator {

    private final GeminiSreService geminiSreService;
    private final ActionValidator actionValidator;
    private final PolicyEngine policyEngine;

    public IncidentOrchestrator(GeminiSreService geminiSreService, ActionValidator actionValidator, PolicyEngine policyEngine) {
        this.geminiSreService = geminiSreService;
        this.actionValidator = actionValidator;
        this.policyEngine = policyEngine;
    }

    public OrchestrationResult processAlert(SystemAlert alert) {
        IncidentStatus status = IncidentStatus.DETECTED;
        SreAction action = null;

        try {
            status = IncidentStatus.ANALYZING;
            action = geminiSreService.analyzeAlert(alert);
            status = IncidentStatus.ACTION_PROPOSED;
        } catch (Exception e) {
            return new OrchestrationResult(alert, null, null, IncidentStatus.FAILED);
        }

        status = IncidentStatus.POLICY_CHECK;
        
        try {
            actionValidator.validate(action);
        } catch (Exception e) {
            return new OrchestrationResult(alert, action, new PolicyDecision(false, action.action(), "Validation failed: " + e.getMessage()), IncidentStatus.BLOCKED);
        }

        PolicyDecision decision = policyEngine.evaluate(action);
        
        if (decision.allowed()) {
            status = IncidentStatus.APPROVED;
        } else {
            status = IncidentStatus.BLOCKED;
        }

        return new OrchestrationResult(alert, action, decision, status);
    }
}
