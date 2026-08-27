package com.sreagent.finops.service;

import com.sreagent.finops.model.ActionType;
import com.sreagent.finops.model.SreAction;
import com.sreagent.finops.model.SystemAlert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MockSreReasoningServiceTest {

    private MockSreReasoningService mockEngine;

    @BeforeEach
    void setUp() {
        mockEngine = new MockSreReasoningService();
    }

    @Test
    void testReturnsScaleUpForHighCpu() {
        SystemAlert alert = new SystemAlert("web-01", 95.0, 50.0, 1500.0, Instant.now(), "prod", "RUNNING");
        SreAction action = mockEngine.analyzeAlert(alert);
        
        assertEquals(ActionType.SCALE_UP, action.action());
        assertEquals(0.95, action.confidence());
    }

    @Test
    void testReturnsNoActionForNormalTelemetry() {
        SystemAlert alert = new SystemAlert("web-01", 50.0, 50.0, 500.0, Instant.now(), "prod", "RUNNING");
        SreAction action = mockEngine.analyzeAlert(alert);
        
        assertEquals(ActionType.NO_ACTION, action.action());
    }

    @Test
    void testReturnsStopVmForProtectedProductionDb() {
        SystemAlert alert = new SystemAlert("prod-db-01", 50.0, 50.0, 500.0, Instant.now(), "prod", "RUNNING");
        SreAction action = mockEngine.analyzeAlert(alert);
        
        assertEquals(ActionType.STOP_VM, action.action());
    }
}
