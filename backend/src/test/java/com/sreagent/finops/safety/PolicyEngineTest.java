package com.sreagent.finops.safety;

import com.sreagent.finops.model.ActionType;
import com.sreagent.finops.model.PolicyDecision;
import com.sreagent.finops.model.Severity;
import com.sreagent.finops.model.SreAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PolicyEngineTest {

    private PolicyEngine policyEngine;
    private SafetyPolicy safetyPolicy;

    @BeforeEach
    void setUp() {
        ActionValidator actionValidator = new ActionValidator();
        safetyPolicy = new SafetyPolicy();
        policyEngine = new PolicyEngine(actionValidator, safetyPolicy);
    }

    private SreAction createAction(ActionType type, String target, double confidence) {
        return new SreAction(type, target, "Test reason", "Test root cause", confidence, Severity.LOW, 0.0, false);
    }

    @Test
    void testDevelopmentVmRestartIsApproved() {
        SreAction action = createAction(ActionType.RESTART_VM, "dev-web-01", 0.95);
        PolicyDecision decision = policyEngine.evaluate(action);
        assertTrue(decision.allowed());
        assertEquals(ActionType.RESTART_VM, decision.action());
    }

    @Test
    void testProductionDatabaseShutdownIsBlocked() {
        SreAction action = createAction(ActionType.STOP_VM, "prod-db-01", 0.95);
        PolicyDecision decision = policyEngine.evaluate(action);
        assertFalse(decision.allowed());
        assertEquals("Production database resources are protected from autonomous shutdown.", decision.reason());
    }

    @Test
    void testLowConfidenceActionIsBlocked() {
        SreAction action = createAction(ActionType.SCALE_UP, "prod-web-01", 0.60);
        PolicyDecision decision = policyEngine.evaluate(action);
        assertFalse(decision.allowed());
        assertEquals("AI confidence is below the autonomous execution threshold.", decision.reason());
    }

    @Test
    void testBlankTargetIsBlocked() {
        SreAction action = createAction(ActionType.RESTART_VM, "", 0.95);
        PolicyDecision decision = policyEngine.evaluate(action);
        assertFalse(decision.allowed());
        assertTrue(decision.reason().contains("Validation failed: Target cannot be blank"));
    }

    @Test
    void testNoActionIsApproved() {
        SreAction action = createAction(ActionType.NO_ACTION, "prod-web-01", 0.95);
        PolicyDecision decision = policyEngine.evaluate(action);
        assertTrue(decision.allowed());
        assertEquals("NO_ACTION is always allowed.", decision.reason());
    }

    @Test
    void testNoActionWithBlankTargetIsApproved() {
        SreAction action = createAction(ActionType.NO_ACTION, "", 0.95);
        PolicyDecision decision = policyEngine.evaluate(action);
        assertTrue(decision.allowed());
    }

    @Test
    void testValidScaleUpIsApproved() {
        SreAction action = createAction(ActionType.SCALE_UP, "prod-web-01", 0.95);
        PolicyDecision decision = policyEngine.evaluate(action);
        assertTrue(decision.allowed());
    }

    @Test
    void testInvalidConfidenceIsRejected() {
        SreAction action = createAction(ActionType.RESTART_VM, "dev-web-01", 1.5);
        PolicyDecision decision = policyEngine.evaluate(action);
        assertFalse(decision.allowed());
        assertTrue(decision.reason().contains("Validation failed: Confidence must be between 0.0 and 1.0"));
    }

    @Test
    void testProtectedProductionTargetIsDetectedCorrectly() {
        assertTrue(safetyPolicy.isProtectedProductionDatabase("prod-db"));
        assertTrue(safetyPolicy.isProtectedProductionDatabase("prod-db-01"));
        assertTrue(safetyPolicy.isProtectedProductionDatabase("production-db"));
        assertTrue(safetyPolicy.isProtectedProductionDatabase("production-db-01"));
        assertTrue(safetyPolicy.isProtectedProductionDatabase("PROD-DB-01"));
        assertFalse(safetyPolicy.isProtectedProductionDatabase("dev-db-01"));
        assertFalse(safetyPolicy.isProtectedProductionDatabase(null));
    }
}
