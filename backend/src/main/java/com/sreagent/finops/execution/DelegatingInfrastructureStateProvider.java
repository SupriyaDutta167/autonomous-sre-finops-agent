package com.sreagent.finops.execution;

import com.sreagent.finops.service.GcpAuthenticationService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class DelegatingInfrastructureStateProvider implements InfrastructureStateProvider {

    private final SimulationExecutor simulationExecutor; // It implements StateProvider too
    private final GcpInfrastructureStateProvider gcpInfrastructureStateProvider;
    private final GcpAuthenticationService authService;

    public DelegatingInfrastructureStateProvider(
            SimulationExecutor simulationExecutor,
            GcpInfrastructureStateProvider gcpInfrastructureStateProvider,
            GcpAuthenticationService authService) {
        this.simulationExecutor = simulationExecutor;
        this.gcpInfrastructureStateProvider = gcpInfrastructureStateProvider;
        this.authService = authService;
    }

    @Override
    public VmState getVmState(String target) {
        if ("GCP".equalsIgnoreCase(authService.getRuntimeMode())) {
            return gcpInfrastructureStateProvider.getVmState(target);
        }
        return simulationExecutor.getVmState(target);
    }
}
