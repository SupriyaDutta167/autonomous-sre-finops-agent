package com.sreagent.finops.service;

import com.sreagent.finops.model.IncidentAuditLog;
import com.sreagent.finops.model.IncidentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuditLogServiceTest {

    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() {
        auditLogService = new AuditLogService();
    }

    private IncidentAuditLog createLog(IncidentStatus status) {
        return new IncidentAuditLog("id1", "target", null, null, null, status, "reason", Instant.now(), 0, null, null);
    }

    @Test
    void testApprovedIncidentIsRecorded() {
        auditLogService.recordIncident(createLog(IncidentStatus.RESOLVED));
        assertEquals(1, auditLogService.getAllIncidents().size());
        assertEquals(IncidentStatus.RESOLVED, auditLogService.getAllIncidents().get(0).status());
    }

    @Test
    void testBlockedIncidentIsRecorded() {
        auditLogService.recordIncident(createLog(IncidentStatus.BLOCKED));
        assertEquals(1, auditLogService.getAllIncidents().size());
        assertEquals(IncidentStatus.BLOCKED, auditLogService.getAllIncidents().get(0).status());
    }

    @Test
    void testApprovalRequiredIncidentIsRecorded() {
        auditLogService.recordIncident(createLog(IncidentStatus.APPROVAL_REQUIRED));
        assertEquals(1, auditLogService.getAllIncidents().size());
        assertEquals(IncidentStatus.APPROVAL_REQUIRED, auditLogService.getAllIncidents().get(0).status());
    }
}
