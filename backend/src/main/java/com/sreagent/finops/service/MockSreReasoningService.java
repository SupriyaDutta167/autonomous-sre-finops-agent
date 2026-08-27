package com.sreagent.finops.service;

import com.sreagent.finops.model.ActionType;
import com.sreagent.finops.model.Severity;
import com.sreagent.finops.model.SreAction;
import com.sreagent.finops.model.SystemAlert;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!gemini")
public class MockSreReasoningService implements SreReasoningEngine {

    @Override
    public SreAction analyzeAlert(SystemAlert alert) {
        // Traffic Surge
        if (alert.requestRate() > 2000.0) {
            return new SreAction(
                    ActionType.SCALE_UP,
                    alert.instanceName(),
                    "Traffic surge detected",
                    "Traffic surge",
                    0.95,
                    Severity.CRITICAL,
                    0.0,
                    false
            );
        }

        // High CPU / high request rate
        if (alert.cpuUtilization() > 90.0 && alert.requestRate() > 1000.0) {
            return new SreAction(
                    ActionType.SCALE_UP,
                    alert.instanceName(),
                    "High CPU and request rate detected",
                    "Traffic spike",
                    0.95,
                    Severity.HIGH,
                    0.0,
                    false
            );
        }

        // Memory Leak
        if (alert.memoryUtilization() > 90.0) {
            return new SreAction(
                    ActionType.RESTART_VM,
                    alert.instanceName(),
                    "High memory utilization detected",
                    "Memory leak",
                    0.95,
                    Severity.HIGH,
                    0.0,
                    false
            );
        }

        // Low utilization (Idle VM)
        if (alert.cpuUtilization() < 10.0 && alert.memoryUtilization() <= 15.0 && alert.requestRate() < 10.0) {
            return new SreAction(
                    ActionType.STOP_VM,
                    alert.instanceName(),
                    "Low utilization detected. Simulated hackathon FinOps estimate: $150.0/mo savings.",
                    "Idle resource",
                    0.95,
                    Severity.LOW,
                    150.0,
                    false
            );
        }
        
        // Protected production database scenario
        if (alert.instanceName() != null && alert.instanceName().contains("prod-db")) {
            return new SreAction(
                    ActionType.STOP_VM,
                    alert.instanceName(),
                    "Mock protected db scenario",
                    "Idle resource",
                    0.95,
                    Severity.LOW,
                    100.0,
                    false
            );
        }

        // Normal telemetry
        return new SreAction(
                ActionType.NO_ACTION,
                alert.instanceName(),
                "Telemetry within normal bounds",
                "None",
                0.95,
                Severity.LOW,
                0.0,
                false
        );
    }
}
