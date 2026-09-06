package com.sreagent.finops.controller;

import com.sreagent.finops.service.GcpAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GcpControllerTest {

    private GcpAuthenticationService authService;
    private GcpController controller;

    @BeforeEach
    void setUp() {
        authService = mock(GcpAuthenticationService.class);
        controller = new GcpController(authService);
    }

    @Test
    void testGetStatus() {
        when(authService.isConnected()).thenReturn(true);
        when(authService.getProjectId()).thenReturn("test-proj");
        when(authService.getZone()).thenReturn("test-zone");
        when(authService.isMutationsEnabled()).thenReturn(false);
        when(authService.getRuntimeMode()).thenReturn("GCP");

        ResponseEntity<Map<String, Object>> response = controller.getStatus();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().get("connected"));
        assertEquals("test-proj", response.getBody().get("projectId"));
        assertEquals("GCP", response.getBody().get("runtimeMode"));
    }

    @Test
    void testConnectRedirects() {
        when(authService.getAuthorizationUrl()).thenReturn("https://auth.url");
        RedirectView view = controller.connect();
        assertEquals("https://auth.url", view.getUrl());
    }

    @Test
    void testConnectFailsGracefully() {
        when(authService.getAuthorizationUrl()).thenThrow(new IllegalStateException("Not configured"));
        RedirectView view = controller.connect();
        assertTrue(view.getUrl().contains("error=Not%20configured"));
    }

    @Test
    void testCallbackSuccess() throws Exception {
        RedirectView view = controller.callback("some-code", null);
        verify(authService, times(1)).handleCallback("some-code");
        assertTrue(view.getUrl().contains("gcp_success=true"));
    }

    @Test
    void testCallbackError() {
        RedirectView view = controller.callback(null, "access_denied");
        assertTrue(view.getUrl().contains("error=access_denied"));
    }

    @Test
    void testDisconnect() {
        ResponseEntity<Map<String, String>> response = controller.disconnect();
        verify(authService, times(1)).disconnect();
        assertEquals("disconnected", response.getBody().get("status"));
    }
}
