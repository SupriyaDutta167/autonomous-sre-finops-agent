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
import com.sreagent.finops.execution.InfrastructureStateProvider;
import com.sreagent.finops.execution.VmState;
import com.sreagent.finops.model.IncidentAuditLog;
import org.springframework.stereotype.Service;

@Service
public class IncidentOrchestrator {

    private final SreReasoningEngine sreReasoningEngine;
    private final ActionValidator actionValidator;
    private final PolicyEngine policyEngine;
    private final InfrastructureExecutor infrastructureExecutor;
    private final VerificationService verificationService;
    private final FinOpsService finOpsService;
    private final AuditLogService auditLogService;
    private final InfrastructureStateProvider stateProvider;

    public IncidentOrchestrator(SreReasoningEngine sreReasoningEngine, ActionValidator actionValidator, 
                                PolicyEngine policyEngine, InfrastructureExecutor infrastructureExecutor,
                                VerificationService verificationService, FinOpsService finOpsService, 
                                AuditLogService auditLogService, InfrastructureStateProvider stateProvider) {
        this.sreReasoningEngine = sreReasoningEngine;
        this.actionValidator = actionValidator;
        this.policyEngine = policyEngine;
        this.infrastructureExecutor = infrastructureExecutor;
        this.verificationService = verificationService;
        this.finOpsService = finOpsService;
        this.auditLogService = auditLogService;
        this.stateProvider = stateProvider;
    }

    public OrchestrationResult processAlert(SystemAlert alert) {
        IncidentStatus status = IncidentStatus.DETECTED;
        SreAction action = null;
        ExecutionResult executionResult = null;
        VerificationResult verificationResult = null;
        FinOpsResult finOpsResult = null;
        PolicyDecision decision = null;

        try {
            status = IncidentStatus.ANALYZING;
            action = sreReasoningEngine.analyzeAlert(alert);
            status = IncidentStatus.ACTION_PROPOSED;
        } catch (Exception e) {
            status = IncidentStatus.FAILED;
            return recordAndReturn(alert, action, decision, status, executionResult, verificationResult, finOpsResult);
        }

        status = IncidentStatus.POLICY_CHECK;
        
        try {
            actionValidator.validate(action);
        } catch (Exception e) {
            decision = new PolicyDecision(DecisionStatus.BLOCKED, action.action(), "Validation failed: " + e.getMessage());
            status = IncidentStatus.BLOCKED;
            return recordAndReturn(alert, action, decision, status, executionResult, verificationResult, finOpsResult);
        }

        decision = policyEngine.evaluate(action);
        
        switch (decision.status()) {
            case APPROVED -> status = IncidentStatus.APPROVED;
            case REQUIRES_APPROVAL -> {
                status = IncidentStatus.APPROVAL_REQUIRED;
                return recordAndReturn(alert, action, decision, status, executionResult, verificationResult, finOpsResult);
            }
            case BLOCKED -> {
                status = IncidentStatus.BLOCKED;
                return recordAndReturn(alert, action, decision, status, executionResult, verificationResult, finOpsResult);
            }
            default -> {
                status = IncidentStatus.BLOCKED;
                return recordAndReturn(alert, action, decision, status, executionResult, verificationResult, finOpsResult);
            }
        }
        
        try {
            // Fetch previous state for verification
            VmState previousState = null;
            if (action.target() != null) {
                VmState current = stateProvider.getVmState(action.target());
                if (!current.state().equals("UNKNOWN")) {
                    previousState = new VmState(current.instanceName(), current.state(), current.capacity());
                }
            }

            status = IncidentStatus.EXECUTING;
            executionResult = infrastructureExecutor.execute(action);
            
            if (!executionResult.success()) {
                status = IncidentStatus.FAILED;
                return recordAndReturn(alert, action, decision, status, executionResult, verificationResult, finOpsResult);
            }

            status = IncidentStatus.VERIFYING;
            verificationResult = verificationService.verify(action, executionResult, previousState);
            
            if (!verificationResult.successful()) {
                status = IncidentStatus.FAILED;
                return recordAndReturn(alert, action, decision, status, executionResult, verificationResult, finOpsResult);
            }

            finOpsResult = finOpsService.calculateEstimatedImpact(action);
            status = IncidentStatus.RESOLVED;

        } catch (Exception e) {
            status = IncidentStatus.FAILED;
        }

        return recordAndReturn(alert, action, decision, status, executionResult, verificationResult, finOpsResult);
    }

    private OrchestrationResult recordAndReturn(SystemAlert alert, SreAction action, PolicyDecision decision, 
                                                IncidentStatus status, ExecutionResult executionResult, 
                                                VerificationResult verificationResult, FinOpsResult finOpsResult) {
        
        double estimatedSavings = (finOpsResult != null) ? finOpsResult.estimatedMonthlySavings() : 0.0;


        IncidentAuditLog log = new IncidentAuditLog(
                java.util.UUID.randomUUID().toString(),
                action != null ? action.target() : (alert != null ? alert.instanceName() : "UNKNOWN"),
                alert, action, decision, status,
                decision != null ? decision.reason() : status.name(),
                java.time.Instant.now(),
                estimatedSavings,
                executionResult,
                verificationResult
        );
        
        auditLogService.recordIncident(log);

        return new OrchestrationResult(alert, action, decision, status, executionResult, verificationResult, finOpsResult);
    }
}
