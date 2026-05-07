package com.rb.feed_ask_ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Ollama AI model.
 * Provides bean definitions for the Spring AI chat client.
 */
@Configuration
public class OllamaConfig {

    /**
     * Creates and configures a ChatClient bean for LLM interactions.
     *
     * @param builder the ChatClient builder
     * @return configured ChatClient instance
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }

}
