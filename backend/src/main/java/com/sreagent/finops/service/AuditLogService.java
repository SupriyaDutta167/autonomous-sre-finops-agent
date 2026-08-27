package com.sreagent.finops.service;

import com.sreagent.finops.model.IncidentAuditLog;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AuditLogService {

    private final List<IncidentAuditLog> auditLogs = new CopyOnWriteArrayList<>();

    public void recordIncident(IncidentAuditLog log) {
        auditLogs.add(log);
    }

    public List<IncidentAuditLog> getAllIncidents() {
        return Collections.unmodifiableList(auditLogs);
    }
}
