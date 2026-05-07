package com.rb.feed_ask_ai;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Main Spring Boot application class for Feed Ask AI.
 * Configured for RAG-based question answering with Ollama embeddings.
 */
@SpringBootApplication
public class FeedAskAiApplication {

    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(FeedAskAiApplication.class, args);
    }

    /**
     * Creates a RestTemplate bean for HTTP operations.
     *
     * @return RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Configures OpenAPI documentation.
     *
     * @return configured OpenAPI specification
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Feed Ask AI API")
                        .version("1.0.0")
                        .description("API for uploading PDF documents, embedding them using Ollama, "
                                + "and asking questions based on the embedded content")
                );
    }

}
