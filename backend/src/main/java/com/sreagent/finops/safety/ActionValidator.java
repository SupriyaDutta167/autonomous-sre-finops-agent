package com.sreagent.finops.safety;

import com.sreagent.finops.model.ActionType;
import com.sreagent.finops.model.SreAction;
import org.springframework.stereotype.Component;

@Component
public class ActionValidator {
    
    public void validate(SreAction action) {
        if (action == null) {
            throw new IllegalArgumentException("SreAction cannot be null");
        }
        if (action.action() == null) {
            throw new IllegalArgumentException("ActionType cannot be null");
        }
        if (action.confidence() < 0.0 || action.confidence() > 1.0) {
            throw new IllegalArgumentException("Confidence must be between 0.0 and 1.0");
        }
        if (action.estimatedSavings() < 0.0) {
            throw new IllegalArgumentException("Estimated savings cannot be negative");
        }
        if (action.action() != ActionType.NO_ACTION) {
            if (action.target() == null || action.target().trim().isEmpty()) {
                throw new IllegalArgumentException("Target cannot be blank for action " + action.action());
            }
        }
    }
}
