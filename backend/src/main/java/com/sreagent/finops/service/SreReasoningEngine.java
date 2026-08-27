package com.sreagent.finops.service;

import com.sreagent.finops.model.SreAction;
import com.sreagent.finops.model.SystemAlert;

public interface SreReasoningEngine {
    SreAction analyzeAlert(SystemAlert alert);
}
