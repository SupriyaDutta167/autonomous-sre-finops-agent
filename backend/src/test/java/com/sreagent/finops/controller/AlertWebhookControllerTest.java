package com.sreagent.finops.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sreagent.finops.model.*;
import com.sreagent.finops.service.IncidentOrchestrator;
import com.sreagent.finops.service.OrchestrationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlertWebhookController.class)
class AlertWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IncidentOrchestrator incidentOrchestrator;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testValidAlertReturnsOk() throws Exception {
        SystemAlert alert = new SystemAlert("dev-web-01", 95.0, 50.0, 1000.0, Instant.now(), "prod", "RUNNING");
        OrchestrationResult result = new OrchestrationResult(alert, null, null, IncidentStatus.FAILED, null);

        when(incidentOrchestrator.processAlert(any())).thenReturn(result);

        mockMvc.perform(post("/api/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alert)))
                .andExpect(status().isOk());
    }

    @Test
    void testMalformedAlertReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/alerts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
