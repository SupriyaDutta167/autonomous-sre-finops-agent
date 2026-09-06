package com.sreagent.finops.execution;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstancesClient;
import com.google.cloud.compute.v1.InstancesSettings;
import com.sreagent.finops.service.GcpAuthenticationService;
import org.springframework.stereotype.Component;

@Component
public class DefaultGcpClient implements GcpClient {

    private final GcpAuthenticationService authService;

    public DefaultGcpClient(GcpAuthenticationService authService) {
        this.authService = authService;
    }

    private InstancesSettings getSettings() throws Exception {
        if (authService.getCredentials() != null) {
            return InstancesSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(authService.getCredentials()))
                    .build();
        }
        return InstancesSettings.newBuilder().build(); // Fallback to ADC if no session (useful for local dev)
    }

    @Override
    public void startInstance(String projectId, String zone, String instanceName) throws Exception {
        try (InstancesClient client = InstancesClient.create(getSettings())) {
            client.startAsync(projectId, zone, instanceName).get();
        }
    }

    @Override
    public void stopInstance(String projectId, String zone, String instanceName) throws Exception {
        try (InstancesClient client = InstancesClient.create(getSettings())) {
            client.stopAsync(projectId, zone, instanceName).get();
        }
    }

    @Override
    public void restartInstance(String projectId, String zone, String instanceName) throws Exception {
        try (InstancesClient client = InstancesClient.create(getSettings())) {
            client.resetAsync(projectId, zone, instanceName).get();
        }
    }

    @Override
    public VmState getInstanceState(String projectId, String zone, String instanceName) throws Exception {
        try (InstancesClient client = InstancesClient.create(getSettings())) {
            Instance instance = client.get(projectId, zone, instanceName);
            if (instance == null) {
                return null;
            }
            return mapInstanceToVmState(instance);
        }
    }

    VmState mapInstanceToVmState(Instance instance) {
        String googleState = instance.getStatus();
        String mappedState = "UNKNOWN";
        
        if (googleState != null) {
            mappedState = switch (googleState.toUpperCase()) {
                case "RUNNING" -> "RUNNING";
                case "TERMINATED", "STOPPED" -> "STOPPED";
                case "PROVISIONING", "STAGING" -> "STARTING";
                case "STOPPING" -> "STOPPING";
                default -> "UNKNOWN";
            };
        }
        
        int capacity = 1;
        if (instance.hasMachineType()) {
            String machineType = instance.getMachineType();
            int lastDash = machineType.lastIndexOf('-');
            if (lastDash >= 0 && lastDash < machineType.length() - 1) {
                String suffix = machineType.substring(lastDash + 1);
                try {
                    capacity = Integer.parseInt(suffix);
                } catch (NumberFormatException e) {
                    capacity = 1;
                }
            }
        }
        
        return new VmState(instance.getName(), mappedState, capacity);
    }
}
