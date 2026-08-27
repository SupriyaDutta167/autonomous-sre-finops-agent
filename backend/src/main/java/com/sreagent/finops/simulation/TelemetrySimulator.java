package com.sreagent.finops.simulation;

import com.sreagent.finops.model.SystemAlert;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TelemetrySimulator {

    public SystemAlert generateTelemetry(IncidentScenario scenario) {
        switch (scenario) {
            case CPU_SPIKE:
                return new SystemAlert(
                        "prod-web-01",
                        97.0, // CPU ≈ 97%
                        50.0, // memory moderate
                        1500.0, // request rate high
                        Instant.now(),
                        "production",
                        "RUNNING"
                );
            case MEMORY_LEAK:
                return new SystemAlert(
                        "prod-api-02",
                        40.0, // CPU moderate
                        95.0, // memory ≈ 95%
                        500.0, // request rate moderate
                        Instant.now(),
                        "production",
                        "RUNNING"
                );
            case TRAFFIC_SURGE:
                return new SystemAlert(
                        "prod-web-03",
                        92.0, // CPU high
                        60.0, // memory moderate
                        3000.0, // request rate very high
                        Instant.now(),
                        "production",
                        "RUNNING"
                );
            case IDLE_VM:
                return new SystemAlert(
                        "dev-batch-01",
                        5.0, // CPU ≈ 5%
                        10.0, // memory ≈ 10%
                        0.0, // request rate ≈ 0
                        Instant.now(),
                        "development",
                        "RUNNING"
                );
            case UNSAFE_ACTION:
                return new SystemAlert(
                        "prod-db-01", // target a protected production database
                        5.0,
                        10.0,
                        0.0,
                        Instant.now(),
                        "production",
                        "RUNNING"
                );
            default:
                throw new IllegalArgumentException("Unknown scenario: " + scenario);
        }
    }
}
