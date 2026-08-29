package com.sreagent.finops.execution;

public interface GcpClient {
    void startInstance(String projectId, String zone, String instanceName) throws Exception;
    void stopInstance(String projectId, String zone, String instanceName) throws Exception;
    void restartInstance(String projectId, String zone, String instanceName) throws Exception;
    VmState getInstanceState(String projectId, String zone, String instanceName) throws Exception;
}
