package com.sreagent.finops.execution;

public class GcpConfigurationException extends RuntimeException {
    public GcpConfigurationException(String message) {
        super(message);
    }
    
    public GcpConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
