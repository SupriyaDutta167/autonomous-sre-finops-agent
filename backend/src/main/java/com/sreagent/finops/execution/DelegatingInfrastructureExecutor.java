package com.sreagent.finops.execution;

import com.sreagent.finops.model.SreAction;
import com.sreagent.finops.service.GcpAuthenticationService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class DelegatingInfrastructureExecutor implements InfrastructureExecutor {

    private final SimulationExecutor simulationExecutor;
    private final GcpComputeService gcpComputeService;
    private final GcpAuthenticationService authService;

    public DelegatingInfrastructureExecutor(
            SimulationExecutor simulationExecutor,
            GcpComputeService gcpComputeService,
            GcpAuthenticationService authService) {
        this.simulationExecutor = simulationExecutor;
        this.gcpComputeService = gcpComputeService;
        this.authService = authService;
    }

    @Override
    public ExecutionResult execute(SreAction action) {
        if ("GCP".equalsIgnoreCase(authService.getRuntimeMode())) {
            return gcpComputeService.execute(action);
        }
        return simulationExecutor.execute(action);
    }
}
