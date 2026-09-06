package com.sreagent.finops.controller;

import com.sreagent.finops.service.GcpAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gcp")
@CrossOrigin(origins = "*")
public class GcpController {

    private final GcpAuthenticationService authService;

    public GcpController(GcpAuthenticationService authService) {
        this.authService = authService;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("connected", authService.isConnected());
        status.put("projectId", authService.getProjectId());
        status.put("zone", authService.getZone());
        status.put("mutationsEnabled", authService.isMutationsEnabled());
        status.put("runtimeMode", authService.getRuntimeMode());
        return ResponseEntity.ok(status);
    }

    @GetMapping("/connect")
    public RedirectView connect() {
        try {
            String url = authService.getAuthorizationUrl();
            return new RedirectView(url);
        } catch (IllegalStateException e) {
            // Usually we'd return a proper error page, but RedirectView needs a URL.
            // We can redirect back to frontend with error.
            return new RedirectView("http://localhost:5173/settings?error=" + e.getMessage().replace(" ", "%20"));
        }
    }

    @GetMapping("/callback")
    public RedirectView callback(@RequestParam(required = false) String code, @RequestParam(required = false) String error) {
        if (error != null) {
            return new RedirectView("http://localhost:5173/settings?error=" + error);
        }
        if (code != null) {
            try {
                authService.handleCallback(code);
                return new RedirectView("http://localhost:5173/settings?gcp_success=true");
            } catch (Exception e) {
                return new RedirectView("http://localhost:5173/settings?error=" + e.getMessage().replace(" ", "%20"));
            }
        }
        return new RedirectView("http://localhost:5173/settings");
    }

    @PostMapping("/disconnect")
    public ResponseEntity<Map<String, String>> disconnect() {
        authService.disconnect();
        return ResponseEntity.ok(Map.of("status", "disconnected"));
    }
    
    @PostMapping("/configure")
    public ResponseEntity<Map<String, String>> configure(
            @RequestBody Map<String, Object> payload) {
        if (payload.containsKey("projectId") && payload.containsKey("zone")) {
            authService.setProjectAndZone((String) payload.get("projectId"), (String) payload.get("zone"));
        }
        if (payload.containsKey("mutationsEnabled")) {
            authService.setMutationsEnabled((Boolean) payload.get("mutationsEnabled"));
        }
        if (payload.containsKey("runtimeMode")) {
            authService.setRuntimeMode((String) payload.get("runtimeMode"));
        }
        return ResponseEntity.ok(Map.of("status", "configured"));
    }

    @PostMapping("/test-connection")
    public ResponseEntity<Map<String, String>> testConnection() {
        if (!authService.isConnected()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "FAILED", "reason", "Not authenticated"));
        }
        if (authService.getProjectId() == null || authService.getProjectId().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "FAILED", "reason", "Project ID not set"));
        }
        
        try {
            // Attempt a safe read operation if we have the client logic, 
            // but the client logic is in GcpComputeService/GcpClient which we haven't wired yet.
            // For now, we return CONNECTED if we have credentials.
            return ResponseEntity.ok(Map.of("status", "CONNECTED"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "FAILED", "reason", e.getMessage()));
        }
    }
}
