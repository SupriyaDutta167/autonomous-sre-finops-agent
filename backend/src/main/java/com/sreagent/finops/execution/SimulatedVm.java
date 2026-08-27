package com.sreagent.finops.execution;

public class SimulatedVm {
    private String instanceName;
    private String state; // RUNNING, STOPPED
    private double cpuUtilization;
    private double memoryUtilization;
    private double requestRate;
    private int capacity;

    public SimulatedVm(String instanceName, String state, double cpuUtilization, double memoryUtilization, double requestRate, int capacity) {
        this.instanceName = instanceName;
        this.state = state;
        this.cpuUtilization = cpuUtilization;
        this.memoryUtilization = memoryUtilization;
        this.requestRate = requestRate;
        this.capacity = capacity;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getCpuUtilization() {
        return cpuUtilization;
    }

    public void setCpuUtilization(double cpuUtilization) {
        this.cpuUtilization = cpuUtilization;
    }

    public double getMemoryUtilization() {
        return memoryUtilization;
    }

    public void setMemoryUtilization(double memoryUtilization) {
        this.memoryUtilization = memoryUtilization;
    }

    public double getRequestRate() {
        return requestRate;
    }

    public void setRequestRate(double requestRate) {
        this.requestRate = requestRate;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
