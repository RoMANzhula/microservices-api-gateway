package org.romanzhula.wallet_service.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ApplicationConfiguration {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

}
