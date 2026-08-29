package com.sreagent.finops.execution;

import com.sreagent.finops.model.SreAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Profile("gcp")
public class GcpComputeService implements InfrastructureExecutor {
    private static final Logger logger = LoggerFactory.getLogger(GcpComputeService.class);

    private final String projectId;
    private final String zone;
    private final boolean mutationsEnabled;
    private final GcpClient gcpClient;

    public GcpComputeService(
            @Value("${finops.gcp.project-id:${GCP_PROJECT_ID:}}") String projectId,
            @Value("${finops.gcp.compute-zone:${GCP_COMPUTE_ZONE:}}") String zone,
            @Value("${finops.gcp.mutations.enabled:${GCP_MUTATIONS_ENABLED:false}}") boolean mutationsEnabled,
            GcpClient gcpClient) {
        
        if (projectId == null || projectId.isBlank()) {
            throw new GcpConfigurationException("GCP project ID is required but missing");
        }
        if (zone == null || zone.isBlank()) {
            throw new GcpConfigurationException("GCP compute zone is required but missing");
        }
        
        this.projectId = projectId;
        this.zone = zone;
        this.mutationsEnabled = mutationsEnabled;
        this.gcpClient = gcpClient;
        logger.info("GCP adapter initialized for project: {}, zone: {}, mutationsEnabled: {}", projectId, zone, mutationsEnabled);
    }

    @Override
    public ExecutionResult execute(SreAction action) {
        if (!mutationsEnabled) {
            logger.info("GCP mutation disabled. No infrastructure change performed for action: {}", action.action());
            return new ExecutionResult(
                    false,
                    action.action(),
                    action.target(),
                    "GCP mutation disabled. No infrastructure change performed.",
                    Instant.now(),
                    "UNKNOWN"
            );
        }

        try {
            String message = "Action executed on GCP";
            String resultingState = "UNKNOWN";

            switch (action.action()) {
                case START_VM -> {
                    gcpClient.startInstance(projectId, zone, action.target());
                    resultingState = "RUNNING";
                }
                case STOP_VM -> {
                    gcpClient.stopInstance(projectId, zone, action.target());
                    resultingState = "STOPPED";
                }
                case RESTART_VM -> {
                    gcpClient.restartInstance(projectId, zone, action.target());
                    resultingState = "RUNNING";
                }
                case NO_ACTION -> {
                    message = "No infrastructure mutation performed";
                    resultingState = "RUNNING"; // default assumption if no action
                }
                default -> {
                    return new ExecutionResult(
                            false,
                            action.action(),
                            action.target(),
                            "Unsupported GCP action: " + action.action(),
                            Instant.now(),
                            "UNKNOWN"
                    );
                }
            }

            return new ExecutionResult(
                    true,
                    action.action(),
                    action.target(),
                    message,
                    Instant.now(),
                    resultingState
            );

        } catch (Exception e) {
            logger.error("Failed to execute GCP action", e);
            throw new GcpExecutionException("Failed to execute GCP action: " + e.getMessage(), e);
        }
    }
}
