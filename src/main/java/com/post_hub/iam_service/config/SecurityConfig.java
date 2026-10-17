package com.post_hub.iam_service.config;

import com.post_hub.iam_service.security.filter.JwtRequestFilter;
import com.post_hub.iam_service.security.handler.AccessRestrictionHandler;
import com.post_hub.iam_service.service.UserService;
import com.post_hub.iam_service.service.model.IamServiceUserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration // Помечает класс как источник конфигурации Spring-бинов
@EnableWebSecurity // Включает поддержку веб-безопасности Spring Security
@EnableMethodSecurity // Включает безопасность на уровне методов (@PreAuthorize и т.д.)
@RequiredArgsConstructor // Генерирует конструктор для final полей (Lombok)
public class SecurityConfig {
    private final JwtRequestFilter jwtRequestFilter; // Кастомный JWT-фильтр для проверки токенов
    private final AccessRestrictionHandler accessRestrictionHandler; // Обработчик отказа в доступе

    private static final PathPatternRequestMatcher[] NOT_SECURED_URLS = new PathPatternRequestMatcher[] {
            // Эндпоинты аутентификации - доступны без токена
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/auth/login"),
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/auth/register"),
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/auth/refresh/token"),

            // Swagger UI и документация API - доступны без токена
            PathPatternRequestMatcher.withDefaults().matcher("/v3/api-docs/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/swagger-ui/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/swagger-ui.html"),
            PathPatternRequestMatcher.withDefaults().matcher("/webjars/**")
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Отключает CSRF защиту (для REST API)
                .authorizeHttpRequests(auth -> auth // Настройка авторизации запросов
                                // Разрешает все OPTIONS запросы (CORS preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                // Разрешает доступ к URL из массива выше
                        .requestMatchers(NOT_SECURED_URLS).permitAll()

//                        .requestMatchers(get("/users/all")).hasAnyAuthority(adminAccessSecurityRoles())
//                        .requestMatchers(get("/posts/all")).hasAnyAuthority(adminAccessSecurityRoles())
                                // Админский эндпоинт - только для ролей SUPER_ADMIN и ADMIN
                        .requestMatchers(post("/users/create")).hasAnyAuthority(adminAccessSecurityRoles())

                                // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions // Настройка обработки исключений
                        // возвращает 401 UNAUTHORIZED при отсутствии аутентификации
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        // Кастомный обработчик для ошибок доступа (403)
                        .accessDeniedHandler(accessRestrictionHandler)
                )
                // Добавляет JWT фильтр перед стандартным фильтром аутентификации
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean // Создает бин для шифрования паролей (BCrypt)
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean // Создает провайдер аутентификации с использованием UserService
    public DaoAuthenticationProvider daoAuthenticationProvider(UserService userService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder()); // Устанавливает кодировщик паролей
        daoAuthenticationProvider.setUserDetailsService(userService); // Устанавливает сервис для загрузки пользователей
        return daoAuthenticationProvider;
    }

    @Bean // Создает менеджер аутентификации
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    private String[] adminAccessSecurityRoles() {
        // этот метод возвращает список ролей, к которым разрешен доступ к админским endpoint-ам
        return new String[] {
                IamServiceUserRole.SUPER_ADMIN.name(),
                IamServiceUserRole.ADMIN.name(),
        };
    }

    // Метод который, принимает путь к ресурсу
    private static RequestMatcher get(String pattern) {
        return PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, pattern);
    }

    // Создает matcher для POST запросов по указанному пути
    private static RequestMatcher post(String pattern) {
        return PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, pattern);
    }
}