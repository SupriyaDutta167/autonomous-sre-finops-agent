package com.sreagent.finops.service;

import com.sreagent.finops.model.SystemAlert;
import com.sreagent.finops.model.SreAction;
import com.sreagent.finops.model.PolicyDecision;
import com.sreagent.finops.model.DecisionStatus;
import com.sreagent.finops.model.IncidentStatus;
import com.sreagent.finops.safety.ActionValidator;
import com.sreagent.finops.safety.PolicyEngine;
import org.springframework.stereotype.Service;

@Service
public class IncidentOrchestrator {

    private final SreReasoningEngine sreReasoningEngine;
    private final ActionValidator actionValidator;
    private final PolicyEngine policyEngine;

    public IncidentOrchestrator(SreReasoningEngine sreReasoningEngine, ActionValidator actionValidator, PolicyEngine policyEngine) {
        this.sreReasoningEngine = sreReasoningEngine;
        this.actionValidator = actionValidator;
        this.policyEngine = policyEngine;
    }

    public OrchestrationResult processAlert(SystemAlert alert) {
        IncidentStatus status = IncidentStatus.DETECTED;
        SreAction action = null;

        try {
            status = IncidentStatus.ANALYZING;
            action = sreReasoningEngine.analyzeAlert(alert);
            status = IncidentStatus.ACTION_PROPOSED;
        } catch (Exception e) {
            return new OrchestrationResult(alert, null, null, IncidentStatus.FAILED);
        }

        status = IncidentStatus.POLICY_CHECK;
        
        try {
            actionValidator.validate(action);
        } catch (Exception e) {
            return new OrchestrationResult(alert, action, new PolicyDecision(DecisionStatus.BLOCKED, action.action(), "Validation failed: " + e.getMessage()), IncidentStatus.BLOCKED);
        }

        PolicyDecision decision = policyEngine.evaluate(action);
        
        switch (decision.status()) {
            case APPROVED -> status = IncidentStatus.APPROVED;
            case REQUIRES_APPROVAL -> status = IncidentStatus.APPROVAL_REQUIRED;
            case BLOCKED -> status = IncidentStatus.BLOCKED;
            default -> status = IncidentStatus.BLOCKED;
        }

        return new OrchestrationResult(alert, action, decision, status);
    }
}
