package org.example.remedy.domain.auth.config;

import org.example.remedy.domain.auth.service.AuthService;
import org.example.remedy.domain.dropping.repository.DroppingRepository;
import org.example.remedy.domain.dropping.service.DroppingService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DroppingMockConfig {

    @Bean
    public DroppingService droppingService() {
        return Mockito.mock(DroppingService.class);
    }

    @Bean
    public DroppingRepository droppingRepository() {
        return Mockito.mock(DroppingRepository.class);
    }

    @Bean
    public AuthService authService() {
        return Mockito.mock(AuthService.class);
    }
}
