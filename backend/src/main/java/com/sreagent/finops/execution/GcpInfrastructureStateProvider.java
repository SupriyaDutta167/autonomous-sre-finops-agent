package com.sreagent.finops.execution;

import com.google.api.gax.rpc.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.sreagent.finops.service.GcpAuthenticationService;

@Service
public class GcpInfrastructureStateProvider implements InfrastructureStateProvider {
    private static final Logger logger = LoggerFactory.getLogger(GcpInfrastructureStateProvider.class);

    private final GcpAuthenticationService authService;
    private final GcpClient gcpClient;

    public GcpInfrastructureStateProvider(
            GcpAuthenticationService authService,
            GcpClient gcpClient) {
        this.authService = authService;
        this.gcpClient = gcpClient;
    }

    @Override
    public VmState getVmState(String target) {
        String projectId = authService.getProjectId();
        String zone = authService.getZone();

        if (!authService.isConnected() || projectId == null || zone == null) {
            return new VmState(target, "UNKNOWN", 1);
        }

        logger.info("Reading instance state from GCP for: {}", target);
        try {
            VmState state = gcpClient.getInstanceState(projectId, zone, target);
            if (state == null) {
                return new VmState(target, "UNKNOWN", 1);
            }
            return state;
        } catch (NotFoundException e) {
            logger.warn("Instance not found on GCP: {}", target);
            return new VmState(target, "UNKNOWN", 1);
        } catch (Exception e) {
            logger.error("Failed to read instance state from GCP for: " + target, e);
            throw new GcpExecutionException("Failed to read instance state from GCP: " + e.getMessage(), e);
        }
    }
}
