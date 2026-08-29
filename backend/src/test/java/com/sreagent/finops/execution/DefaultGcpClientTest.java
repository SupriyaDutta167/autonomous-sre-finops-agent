package com.sreagent.finops.execution;

import com.google.cloud.compute.v1.Instance;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultGcpClientTest {

    @Test
    void testMapInstanceToVmState_1vCPU() {
        DefaultGcpClient client = new DefaultGcpClient();
        Instance instance = Instance.newBuilder()
                .setName("test-micro")
                .setStatus("RUNNING")
                .setMachineType("zones/us-central1-a/machineTypes/e2-micro")
                .build();
        VmState state = client.mapInstanceToVmState(instance);
        assertEquals(1, state.capacity());
    }

    @Test
    void testMapInstanceToVmState_multivCPU() {
        DefaultGcpClient client = new DefaultGcpClient();
        Instance instance = Instance.newBuilder()
                .setName("test-std-4")
                .setStatus("RUNNING")
                .setMachineType("zones/us-central1-a/machineTypes/n1-standard-4")
                .build();
        VmState state = client.mapInstanceToVmState(instance);
        assertEquals(4, state.capacity());
    }

    @Test
    void testMapInstanceToVmState_unresolvedMachineType() {
        DefaultGcpClient client = new DefaultGcpClient();
        Instance instance = Instance.newBuilder()
                .setName("test-unknown")
                .setStatus("RUNNING")
                .setMachineType("zones/us-central1-a/machineTypes/custom-machine")
                .build();
        VmState state = client.mapInstanceToVmState(instance);
        assertEquals(1, state.capacity()); // fallback
    }

    @Test
    void testMapInstanceToVmState_noMachineType() {
        DefaultGcpClient client = new DefaultGcpClient();
        Instance instance = Instance.newBuilder()
                .setName("test-none")
                .setStatus("RUNNING")
                .build();
        VmState state = client.mapInstanceToVmState(instance);
        assertEquals(1, state.capacity()); // fallback
    }
}
