package com.post_hub.iam_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Делает класс конфигурационным
@EnableWebSecurity // Включает поддержку Spring Security
public class SecurityConfig {

    @Bean
    // Метод для настройки правил безопасности
    // Теперь Security не требует логина и пароля
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Зачем отключать: REST API не использует cookies/sessions
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                ); // Доступ к всем endpoint'ам разрешён

        return http.build();
    }
}
