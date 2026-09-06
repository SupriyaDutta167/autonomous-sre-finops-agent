package com.sreagent.finops.service;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Date;
import java.util.Map;

@Service
public class GcpAuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(GcpAuthenticationService.class);

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri = "http://localhost:8080/api/gcp/callback"; // Default for local
    
    private boolean connected = false;
    private String projectId = null;
    private String zone = null;
    private boolean mutationsEnabled = false;
    private GoogleCredentials credentials = null;
    private String runtimeMode = "SIMULATION"; // "SIMULATION" or "GCP"

    public GcpAuthenticationService(
            @Value("${finops.gcp.oauth.client-id:}") String clientId,
            @Value("${finops.gcp.oauth.client-secret:}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getAuthorizationUrl() {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalStateException("Google OAuth Client ID is not configured on the backend.");
        }
        String scope = "https://www.googleapis.com/auth/compute.readonly https://www.googleapis.com/auth/cloud-platform";
        return "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=" + scope +
                "&access_type=offline" +
                "&prompt=consent";
    }

    public void handleCallback(String code) {
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalStateException("Google OAuth Client ID or Secret is not configured.");
        }
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("code", code);
            map.add("grant_type", "authorization_code");
            map.add("redirect_uri", redirectUri);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity("https://oauth2.googleapis.com/token", request, Map.class);
            
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("access_token")) {
                String accessToken = (String) body.get("access_token");
                Integer expiresIn = (Integer) body.get("expires_in");
                Date expirationTime = new Date(System.currentTimeMillis() + (expiresIn * 1000L));
                
                this.credentials = GoogleCredentials.create(new AccessToken(accessToken, expirationTime));
                this.connected = true;
                this.runtimeMode = "GCP";
                logger.info("Successfully authenticated with Google Cloud via OAuth.");
            } else {
                throw new RuntimeException("No access_token found in response.");
            }
        } catch (Exception e) {
            logger.error("OAuth callback failed", e);
            throw new RuntimeException("Failed to exchange OAuth code: " + e.getMessage());
        }
    }

    public void disconnect() {
        this.connected = false;
        this.credentials = null;
        this.projectId = null;
        this.zone = null;
        this.mutationsEnabled = false;
        this.runtimeMode = "SIMULATION";
        logger.info("Disconnected from Google Cloud.");
    }

    public void setProjectAndZone(String projectId, String zone) {
        this.projectId = projectId;
        this.zone = zone;
    }

    public void setMutationsEnabled(boolean enabled) {
        this.mutationsEnabled = enabled;
    }

    public void setRuntimeMode(String mode) {
        if ("GCP".equalsIgnoreCase(mode) && !this.connected) {
            // we can switch to GCP in UI even if disconnected? 
            // The prompt says "When GOOGLE CLOUD: Connection: CONNECTED / NOT CONNECTED"
            // So we CAN switch runtimeMode to GCP without being connected.
        }
        this.runtimeMode = mode;
    }

    public boolean isConnected() { return connected; }
    public String getProjectId() { return projectId; }
    public String getZone() { return zone; }
    public boolean isMutationsEnabled() { return mutationsEnabled; }
    public GoogleCredentials getCredentials() { return credentials; }
    public String getRuntimeMode() { return runtimeMode; }
}
