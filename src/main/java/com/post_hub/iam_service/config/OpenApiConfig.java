package com.post_hub.iam_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Configuration
// Основная аннотация OpenAPI для определения информации о документации API
@OpenAPIDefinition(
        // Блок с основной информацией об API
        info = @Info(
                title = "POST_HUB REST API",  // Название API
                version = "1.0"               // Версия API
        ),
        // Глобальное требование безопасности - JWT токен обязателен для всех endpoints
        security = { @SecurityRequirement(name = HttpHeaders.AUTHORIZATION) }
)
// Определение схемы безопасности для API
@SecurityScheme(
        name = HttpHeaders.AUTHORIZATION, // Название схемы безопасности (используется заголовок Authorization)
        type = SecuritySchemeType.HTTP,   // Тип схемы - HTTP
        scheme = "bearer",                // Схема аутентификации - bearer token
        bearerFormat = "JWT"              // Формат токена - JSON Web Token
)
public class OpenApiConfig {

    // Инъекция значения из application.properties/yml с ключом "swagger.servers.first"
    // Это URL первого сервера для документации
    @Value("${swagger.servers.first}")
    private String firstServer;

    // Bean для группировки и настройки публичного API
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("iam-service") // Название группы API (будет отображаться в UI Swagger)
                .packagesToScan("com.post_hub.iam_service") // Пакеты для сканирования контроллеров
                .addOpenApiCustomizer(serverCustomizer()) // Добавление кастомизатора для настройки серверов
                .build();
    }

    // Bean для кастомизации OpenAPI документации
    @Bean
    public OpenApiCustomizer serverCustomizer() {
        // Лямбда-выражение, которое модифицирует объект OpenAPI
        return openApi -> {
            List<Server> servers = new ArrayList<>();
            // Проверка, что URL сервера задан в конфигурации
            if (Objects.nonNull(firstServer)) {
                // Добавление сервера с URL из конфигурации
                servers.add(new Server().url(firstServer).description("API Server"));
            }
            // Установка списка серверов в OpenAPI спецификацию
            openApi.servers(servers);
        };
    }
}
