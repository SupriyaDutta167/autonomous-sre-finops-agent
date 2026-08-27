package com.sreagent.finops.controller;

import com.sreagent.finops.execution.SimulationExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SimulationExecutor simulationExecutor;

    @MockBean
    private com.sreagent.finops.service.AuditLogService auditLogService;

    @Test
    void testHealthReturnsHealthy() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("healthy"));
    }

    @Test
    void testGetIncidentsReturnsAuditHistory() throws Exception {
        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
