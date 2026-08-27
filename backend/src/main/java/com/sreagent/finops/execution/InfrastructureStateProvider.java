package com.sreagent.finops.execution;

public interface InfrastructureStateProvider {
    VmState getVmState(String target);
}
