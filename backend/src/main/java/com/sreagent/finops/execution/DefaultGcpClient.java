package com.sreagent.finops.execution;

import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstancesClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("gcp")
public class DefaultGcpClient implements GcpClient {

    @Override
    public void startInstance(String projectId, String zone, String instanceName) throws Exception {
        try (InstancesClient client = InstancesClient.create()) {
            client.startAsync(projectId, zone, instanceName).get();
        }
    }

    @Override
    public void stopInstance(String projectId, String zone, String instanceName) throws Exception {
        try (InstancesClient client = InstancesClient.create()) {
            client.stopAsync(projectId, zone, instanceName).get();
        }
    }

    @Override
    public void restartInstance(String projectId, String zone, String instanceName) throws Exception {
        try (InstancesClient client = InstancesClient.create()) {
            client.resetAsync(projectId, zone, instanceName).get();
        }
    }

    @Override
    public VmState getInstanceState(String projectId, String zone, String instanceName) throws Exception {
        try (InstancesClient client = InstancesClient.create()) {
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
