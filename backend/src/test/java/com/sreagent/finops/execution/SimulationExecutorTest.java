package com.sreagent.finops.execution;

import com.sreagent.finops.model.ActionType;
import com.sreagent.finops.model.Severity;
import com.sreagent.finops.model.SreAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulationExecutorTest {

    private SimulationExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new SimulationExecutor();
    }

    private SreAction createAction(ActionType type, String target) {
        return new SreAction(type, target, "Reason", "Desc", 0.95, Severity.LOW, 0.0, false);
    }

    @Test
    void testRestart() {
        ExecutionResult result = executor.execute(createAction(ActionType.RESTART_VM, "dev-web-01"));
        assertTrue(result.success());
        assertEquals("RUNNING", result.resultingState());
        SimulatedVm vm = executor.getVm("dev-web-01");
        assertEquals(0.0, vm.getCpuUtilization()); // Dropped by 50 or to 0
    }

    @Test
    void testStart() {
        SimulatedVm vm = executor.getVm("dev-web-01");
        vm.setState("STOPPED");
        executor.updateVmState(vm);

        ExecutionResult result = executor.execute(createAction(ActionType.START_VM, "dev-web-01"));
        assertTrue(result.success());
        assertEquals("RUNNING", result.resultingState());
        assertEquals("RUNNING", executor.getVm("dev-web-01").getState());
    }

    @Test
    void testStop() {
        ExecutionResult result = executor.execute(createAction(ActionType.STOP_VM, "dev-web-01"));
        assertTrue(result.success());
        assertEquals("STOPPED", result.resultingState());
        assertEquals("STOPPED", executor.getVm("dev-web-01").getState());
    }

    @Test
    void testScaleUp() {
        SimulatedVm vm = executor.getVm("dev-web-01");
        int initialCapacity = vm.getCapacity();
        double initialCpu = vm.getCpuUtilization();
        
        ExecutionResult result = executor.execute(createAction(ActionType.SCALE_UP, "dev-web-01"));
        assertTrue(result.success());
        assertEquals(initialCapacity * 2, executor.getVm("dev-web-01").getCapacity());
        assertEquals(initialCpu / 2, executor.getVm("dev-web-01").getCpuUtilization());
    }

    @Test
    void testScaleDown() {
        SimulatedVm vm = executor.getVm("dev-web-01");
        int initialCapacity = vm.getCapacity();
        double initialCpu = vm.getCpuUtilization();
        
        ExecutionResult result = executor.execute(createAction(ActionType.SCALE_DOWN, "dev-web-01"));
        assertTrue(result.success());
        assertEquals(Math.max(1, initialCapacity / 2), executor.getVm("dev-web-01").getCapacity());
    }

    @Test
    void testNoAction() {
        ExecutionResult result = executor.execute(createAction(ActionType.NO_ACTION, "dev-web-01"));
        assertTrue(result.success());
        assertEquals("RUNNING", result.resultingState());
    }
}
