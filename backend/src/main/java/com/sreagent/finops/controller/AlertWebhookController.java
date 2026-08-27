package com.sreagent.finops.controller;

import com.sreagent.finops.model.SystemAlert;
import com.sreagent.finops.service.IncidentOrchestrator;
import com.sreagent.finops.service.OrchestrationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
public class AlertWebhookController {

    private final IncidentOrchestrator incidentOrchestrator;

    public AlertWebhookController(IncidentOrchestrator incidentOrchestrator) {
        this.incidentOrchestrator = incidentOrchestrator;
    }

    @PostMapping
    public ResponseEntity<OrchestrationResult> receiveAlert(@RequestBody SystemAlert alert) {
        if (alert == null || alert.instanceName() == null) {
            return ResponseEntity.badRequest().build();
        }
        OrchestrationResult result = incidentOrchestrator.processAlert(alert);
        return ResponseEntity.ok(result);
    }
}
