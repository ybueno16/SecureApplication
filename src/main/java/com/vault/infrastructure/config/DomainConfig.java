package com.vault.infrastructure.config;

import com.vault.domain.service.PasswordGeneratorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public PasswordGeneratorService passwordGeneratorService() {
        return new PasswordGeneratorService();
    }
}
