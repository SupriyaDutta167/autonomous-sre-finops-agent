package com.sreagent.finops.service;

import com.sreagent.finops.model.SreAction;
import com.sreagent.finops.model.SystemAlert;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import org.springframework.context.annotation.Profile;

@Service
@Profile("gemini")
public class GeminiSreService implements SreReasoningEngine {

    private final ChatClient chatClient;

    @Value("classpath:prompts/sre-system-prompt.txt")
    private Resource systemPromptResource;

    public GeminiSreService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public SreAction analyzeAlert(SystemAlert alert) {
        String userMessage = String.format(
                "INSTANCE: %s\nENVIRONMENT: %s\nCPU: %s%%\nMEMORY: %s%%\nREQUEST_RATE: %s\nSTATE: %s\nTIMESTAMP: %s",
                alert.instanceName(), alert.environment(), alert.cpuUtilization(), 
                alert.memoryUtilization(), alert.requestRate(), alert.instanceState(), alert.timestamp()
        );

        try {
            SreAction action = chatClient.prompt()
                    .system(s -> s.text(systemPromptResource))
                    .user(userMessage)
                    .call()
                    .entity(SreAction.class);

            if (action == null || action.action() == null) {
                throw new IllegalStateException("AI returned an invalid or null action.");
            }
            return action;
        } catch (Exception e) {
            throw new RuntimeException("Failed to process alert with Gemini: " + e.getMessage(), e);
        }
    }
}

