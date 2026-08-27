package com.sreagent.finops.safety;

import com.sreagent.finops.model.ActionType;
import com.sreagent.finops.model.PolicyDecision;
import com.sreagent.finops.model.SreAction;
import org.springframework.stereotype.Service;

@Service
public class PolicyEngine {

    private final ActionValidator actionValidator;
    private final SafetyPolicy safetyPolicy;

    public PolicyEngine(ActionValidator actionValidator, SafetyPolicy safetyPolicy) {
        this.actionValidator = actionValidator;
        this.safetyPolicy = safetyPolicy;
    }

    public PolicyDecision evaluate(SreAction action) {
        try {
            actionValidator.validate(action);
        } catch (IllegalArgumentException e) {
            return new PolicyDecision(false, action != null ? action.action() : null, "Validation failed: " + e.getMessage());
        }

        if (action.action() == ActionType.NO_ACTION) {
            return new PolicyDecision(true, action.action(), "NO_ACTION is always allowed.");
        }

        if (action.confidence() < SafetyPolicy.MIN_AUTONOMOUS_CONFIDENCE) {
            return new PolicyDecision(false, action.action(), "AI confidence is below the autonomous execution threshold.");
        }

        if (action.action() == ActionType.STOP_VM && safetyPolicy.isProtectedProductionDatabase(action.target())) {
            return new PolicyDecision(false, action.action(), "Production database resources are protected from autonomous shutdown.");
        }

        return new PolicyDecision(true, action.action(), "Action passed all safety policies.");
    }
}
