package com.sreagent.finops.service;

import com.sreagent.finops.model.SreAction;
import org.springframework.stereotype.Service;

@Service
public class FinOpsService {

    public FinOpsResult calculateEstimatedImpact(SreAction action) {
        if (action == null) {
            return new FinOpsResult(0.0, "USD", "No action");
        }

        double estimatedMonthlySavings = 0.0;
        String explanation = "No direct savings";

        switch (action.action()) {
            case STOP_VM:
                // Simulate hackathon estimate for idle VM
                estimatedMonthlySavings = 150.0;
                explanation = "Estimated hackathon savings from stopping idle VM.";
                break;
            case SCALE_DOWN:
                estimatedMonthlySavings = 75.0;
                explanation = "Simulated hackathon estimate from scaling down capacity.";
                break;
            case SCALE_UP:
                explanation = "Reliability action (scale up). No direct cost savings.";
                break;
            case RESTART_VM:
                explanation = "Reliability action (restart). No direct cost savings.";
                break;
            case NO_ACTION:
                explanation = "No action taken, no savings.";
                break;
        }

        return new FinOpsResult(estimatedMonthlySavings, "USD", explanation);
    }
}
