package com.sreagent.finops.service;

import com.sreagent.finops.model.ActionType;
import com.sreagent.finops.model.Severity;
import com.sreagent.finops.model.SreAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FinOpsServiceTest {

    private final FinOpsService finOpsService = new FinOpsService();

    private SreAction createAction(ActionType type) {
        return new SreAction(type, "test-vm", "reason", "desc", 0.95, Severity.LOW, 0, false);
    }

    @Test
    void testIdleVmProducesPositiveEstimatedSavings() {
        FinOpsResult result = finOpsService.calculateEstimatedImpact(createAction(ActionType.STOP_VM));
        assertTrue(result.estimatedMonthlySavings() > 0.0);
    }

    @Test
    void testScaleDownProducesPositiveEstimatedSavings() {
        FinOpsResult result = finOpsService.calculateEstimatedImpact(createAction(ActionType.SCALE_DOWN));
        assertTrue(result.estimatedMonthlySavings() > 0.0);
    }

    @Test
    void testScaleUpProducesZeroDirectSavings() {
        FinOpsResult result = finOpsService.calculateEstimatedImpact(createAction(ActionType.SCALE_UP));
        assertEquals(0.0, result.estimatedMonthlySavings());
    }

    @Test
    void testRestartProducesZeroDirectSavings() {
        FinOpsResult result = finOpsService.calculateEstimatedImpact(createAction(ActionType.RESTART_VM));
        assertEquals(0.0, result.estimatedMonthlySavings());
    }

    @Test
    void testNoActionProducesZeroSavings() {
        FinOpsResult result = finOpsService.calculateEstimatedImpact(createAction(ActionType.NO_ACTION));
        assertEquals(0.0, result.estimatedMonthlySavings());
    }
}
