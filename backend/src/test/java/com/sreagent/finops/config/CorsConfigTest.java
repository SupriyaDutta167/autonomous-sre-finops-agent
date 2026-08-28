package com.sreagent.finops.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CorsConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCorsAllowedOrigin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.options("/api/health")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().exists("Access-Control-Allow-Methods"));
    }

    @Test
    void testCorsDisallowedOrigin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.options("/api/health")
                .header("Origin", "http://malicious-site.com")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden());
    }
}
