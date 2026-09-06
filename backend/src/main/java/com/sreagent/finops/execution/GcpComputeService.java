package com.sreagent.finops.execution;

import com.sreagent.finops.model.SreAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;

import com.sreagent.finops.service.GcpAuthenticationService;

@Service
public class GcpComputeService implements InfrastructureExecutor {
    private static final Logger logger = LoggerFactory.getLogger(GcpComputeService.class);

    private final GcpAuthenticationService authService;
    private final GcpClient gcpClient;

    public GcpComputeService(
            GcpAuthenticationService authService,
            GcpClient gcpClient) {
        this.authService = authService;
        this.gcpClient = gcpClient;
        logger.info("GCP adapter initialized");
    }

    @Override
    public ExecutionResult execute(SreAction action) {
        String projectId = authService.getProjectId();
        String zone = authService.getZone();
        boolean mutationsEnabled = authService.isMutationsEnabled();
        
        if (!authService.isConnected() || projectId == null || zone == null) {
            return new ExecutionResult(
                    false,
                    action.action(),
                    action.target(),
                    "GCP not connected or configured.",
                    Instant.now(),
                    "UNKNOWN"
            );
        }

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
