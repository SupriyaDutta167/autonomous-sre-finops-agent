package com.sreagent.finops.service;

import com.sreagent.finops.model.SystemAlert;
import com.sreagent.finops.model.SreAction;
import com.sreagent.finops.model.PolicyDecision;
import com.sreagent.finops.model.DecisionStatus;
import com.sreagent.finops.model.IncidentStatus;
import com.sreagent.finops.safety.ActionValidator;
import com.sreagent.finops.safety.PolicyEngine;
import com.sreagent.finops.execution.ExecutionResult;
import com.sreagent.finops.execution.InfrastructureExecutor;
import org.springframework.stereotype.Service;

@Service
public class IncidentOrchestrator {

    private final SreReasoningEngine sreReasoningEngine;
    private final ActionValidator actionValidator;
    private final PolicyEngine policyEngine;
    private final InfrastructureExecutor infrastructureExecutor;

    public IncidentOrchestrator(SreReasoningEngine sreReasoningEngine, ActionValidator actionValidator, PolicyEngine policyEngine, InfrastructureExecutor infrastructureExecutor) {
        this.sreReasoningEngine = sreReasoningEngine;
        this.actionValidator = actionValidator;
        this.policyEngine = policyEngine;
        this.infrastructureExecutor = infrastructureExecutor;
    }

    public OrchestrationResult processAlert(SystemAlert alert) {
        IncidentStatus status = IncidentStatus.DETECTED;
        SreAction action = null;

        try {
            status = IncidentStatus.ANALYZING;
            action = sreReasoningEngine.analyzeAlert(alert);
            status = IncidentStatus.ACTION_PROPOSED;
        } catch (Exception e) {
            return new OrchestrationResult(alert, null, null, IncidentStatus.FAILED, null);
        }

        status = IncidentStatus.POLICY_CHECK;
        
        try {
            actionValidator.validate(action);
        } catch (Exception e) {
            return new OrchestrationResult(alert, action, new PolicyDecision(DecisionStatus.BLOCKED, action.action(), "Validation failed: " + e.getMessage()), IncidentStatus.BLOCKED, null);
        }

        PolicyDecision decision = policyEngine.evaluate(action);
        
        switch (decision.status()) {
            case APPROVED -> status = IncidentStatus.APPROVED;
            case REQUIRES_APPROVAL -> status = IncidentStatus.APPROVAL_REQUIRED;
            case BLOCKED -> status = IncidentStatus.BLOCKED;
            default -> status = IncidentStatus.BLOCKED;
        }
        
        ExecutionResult executionResult = null;
        if (status == IncidentStatus.APPROVED) {
            executionResult = infrastructureExecutor.execute(action);
        }

        return new OrchestrationResult(alert, action, decision, status, executionResult);
    }
}
