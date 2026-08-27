package com.sreagent.finops.safety;

import org.springframework.stereotype.Component;

@Component
public class SafetyPolicy {
    
    public static final double MIN_AUTONOMOUS_CONFIDENCE = 0.80;

    public boolean isProtectedProductionDatabase(String target) {
        if (target == null) return false;
        String lower = target.toLowerCase();
        return lower.equals("prod-db") || 
               lower.equals("prod-db-01") || 
               lower.equals("production-db") || 
               lower.equals("production-db-01");
    }
}
