package cross.icross.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${secret.apiKey}")
    private String apiKey;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                // apikey 넣어야 함
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
}