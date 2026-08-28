package com.sreagent.finops.execution;

import com.sreagent.finops.model.SreAction;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SimulationExecutor implements InfrastructureExecutor, InfrastructureStateProvider {

    private final Map<String, SimulatedVm> vms = new ConcurrentHashMap<>();

    public SimulationExecutor() {
        // Initialize with some default VMs for simulation purposes
        vms.put("prod-web-01", new SimulatedVm("prod-web-01", "RUNNING", 50.0, 50.0, 500.0, 4));
        vms.put("prod-db-01", new SimulatedVm("prod-db-01", "RUNNING", 40.0, 60.0, 200.0, 8));
        vms.put("dev-web-01", new SimulatedVm("dev-web-01", "RUNNING", 10.0, 20.0, 50.0, 2));
        vms.put("prod-web-03", new SimulatedVm("prod-web-03", "RUNNING", 60.0, 50.0, 400.0, 2));
    }

    public Collection<SimulatedVm> getAllVms() {
        return vms.values();
    }

    public void updateVmState(SimulatedVm vm) {
        vms.put(vm.getInstanceName(), vm);
    }

    public SimulatedVm getVm(String instanceName) {
        return vms.getOrDefault(instanceName, new SimulatedVm(instanceName, "UNKNOWN", 0, 0, 0, 1));
    }

    @Override
    public VmState getVmState(String target) {
        if (!vms.containsKey(target)) {
            return new VmState(target, "UNKNOWN", 1);
        }
        SimulatedVm vm = vms.get(target);
        return new VmState(vm.getInstanceName(), vm.getState(), vm.getCapacity());
    }

    @Override
    public ExecutionResult execute(SreAction action) {
        SimulatedVm vm = getVm(action.target());
        boolean success = true;
        String message = "Action executed successfully in simulation";
        
        switch (action.action()) {
            case START_VM -> vm.setState("RUNNING");
            case STOP_VM -> {
                vm.setState("STOPPED");
                vm.setCpuUtilization(0);
                vm.setMemoryUtilization(0);
                vm.setRequestRate(0);
            }
            case RESTART_VM -> {
                // Simulate restart - state stays RUNNING but load drops briefly
                vm.setState("RUNNING");
                vm.setCpuUtilization(Math.max(0, vm.getCpuUtilization() - 50.0));
                message = "Simulated restart completed";
            }
            case SCALE_UP -> {
                vm.setCapacity(vm.getCapacity() * 2);
                vm.setCpuUtilization(vm.getCpuUtilization() / 2);
                message = "Simulated scale up completed. Capacity increased.";
            }
            case SCALE_DOWN -> {
                vm.setCapacity(Math.max(1, vm.getCapacity() / 2));
                vm.setCpuUtilization(Math.min(100.0, vm.getCpuUtilization() * 2));
                message = "Simulated scale down completed. Capacity decreased.";
            }
            case NO_ACTION -> {
                message = "No infrastructure mutation performed";
                // Do not mutate state or insert into map if it doesn't exist
                return new ExecutionResult(
                        true,
                        action.action(),
                        action.target(),
                        message,
                        Instant.now(),
                        vms.containsKey(action.target()) ? vms.get(action.target()).getState() : "UNKNOWN"
                );
            }
            default -> {
                success = false;
                message = "Unsupported action in simulation";
            }
        }
        
        vms.put(action.target(), vm);

        return new ExecutionResult(
                success,
                action.action(),
                action.target(),
                message,
                Instant.now(),
                vm.getState()
        );
    }
}
