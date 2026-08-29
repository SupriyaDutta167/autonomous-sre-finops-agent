package com.sreagent.finops.execution;

import com.google.api.gax.rpc.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("gcp")
public class GcpInfrastructureStateProvider implements InfrastructureStateProvider {
    private static final Logger logger = LoggerFactory.getLogger(GcpInfrastructureStateProvider.class);

    private final String projectId;
    private final String zone;
    private final GcpClient gcpClient;

    public GcpInfrastructureStateProvider(
            @Value("${finops.gcp.project-id:${GCP_PROJECT_ID:}}") String projectId,
            @Value("${finops.gcp.compute-zone:${GCP_COMPUTE_ZONE:}}") String zone,
            GcpClient gcpClient) {
        
        if (projectId == null || projectId.isBlank()) {
            throw new GcpConfigurationException("GCP project ID is required but missing");
        }
        if (zone == null || zone.isBlank()) {
            throw new GcpConfigurationException("GCP compute zone is required but missing");
        }
        
        this.projectId = projectId;
        this.zone = zone;
        this.gcpClient = gcpClient;
    }

    @Override
    public VmState getVmState(String target) {
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
