package com.sreagent.finops.simulation;

import com.sreagent.finops.model.SystemAlert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelemetrySimulatorTest {

    private final TelemetrySimulator simulator = new TelemetrySimulator();

    @Test
    void testCpuSpikeCreatesExpectedTelemetry() {
        SystemAlert alert = simulator.generateTelemetry(IncidentScenario.CPU_SPIKE);
        assertTrue(alert.cpuUtilization() > 95.0);
        assertTrue(alert.requestRate() > 1000.0);
    }

    @Test
    void testMemoryLeakCreatesExpectedTelemetry() {
        SystemAlert alert = simulator.generateTelemetry(IncidentScenario.MEMORY_LEAK);
        assertTrue(alert.memoryUtilization() > 90.0);
    }

    @Test
    void testTrafficSurgeCreatesExpectedTelemetry() {
        SystemAlert alert = simulator.generateTelemetry(IncidentScenario.TRAFFIC_SURGE);
        assertTrue(alert.cpuUtilization() > 90.0);
        assertTrue(alert.requestRate() > 2000.0);
    }

    @Test
    void testIdleVmCreatesExpectedTelemetry() {
        SystemAlert alert = simulator.generateTelemetry(IncidentScenario.IDLE_VM);
        assertTrue(alert.cpuUtilization() < 10.0);
        assertTrue(alert.memoryUtilization() < 20.0);
        assertTrue(alert.requestRate() < 10.0);
    }

    @Test
    void testUnsafeScenarioTargetsProdDb() {
        SystemAlert alert = simulator.generateTelemetry(IncidentScenario.UNSAFE_ACTION);
        assertEquals("prod-db-01", alert.instanceName());
    }
}
