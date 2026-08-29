package com.sreagent.finops.execution;

public class GcpExecutionException extends RuntimeException {
    public GcpExecutionException(String message) {
        super(message);
    }
    
    public GcpExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
