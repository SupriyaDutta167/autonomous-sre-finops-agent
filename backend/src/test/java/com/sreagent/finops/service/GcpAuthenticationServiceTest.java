package com.sreagent.finops.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class GcpAuthenticationServiceTest {

    private GcpAuthenticationService authService;

    @BeforeEach
    void setUp() {
        authService = new GcpAuthenticationService("test-client-id", "test-client-secret");
    }

    @Test
    void testInitialState() {
        assertFalse(authService.isConnected());
        assertFalse(authService.isMutationsEnabled());
        assertEquals("SIMULATION", authService.getRuntimeMode());
        assertNull(authService.getProjectId());
        assertNull(authService.getZone());
    }

    @Test
    void testGetAuthorizationUrl() {
        String url = authService.getAuthorizationUrl();
        assertTrue(url.contains("https://accounts.google.com/o/oauth2/v2/auth"));
        assertTrue(url.contains("client_id=test-client-id"));
        assertTrue(url.contains("redirect_uri="));
        assertTrue(url.contains("response_type=code"));
    }

    @Test
    void testGetAuthorizationUrlFailsWhenUnconfigured() {
        GcpAuthenticationService unconfiguredService = new GcpAuthenticationService("", "");
        
        Exception e = assertThrows(IllegalStateException.class, unconfiguredService::getAuthorizationUrl);
        assertEquals("Google OAuth Client ID is not configured on the backend.", e.getMessage());
    }

    @Test
    void testDisconnect() {
        ReflectionTestUtils.setField(authService, "connected", true);
        ReflectionTestUtils.setField(authService, "runtimeMode", "GCP");
        ReflectionTestUtils.setField(authService, "mutationsEnabled", true);
        ReflectionTestUtils.setField(authService, "projectId", "some-project");
        
        authService.disconnect();
        
        assertFalse(authService.isConnected());
        assertFalse(authService.isMutationsEnabled());
        assertEquals("SIMULATION", authService.getRuntimeMode());
        assertNull(authService.getProjectId());
    }
}
