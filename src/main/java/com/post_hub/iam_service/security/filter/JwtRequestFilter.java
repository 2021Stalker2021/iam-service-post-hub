package com.post_hub.iam_service.security.filter;

import com.post_hub.iam_service.model.constants.ApiErrorMessage;
import com.post_hub.iam_service.security.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    // Константы - неизменяемые значения, чтобы не ошибиться в написании
    private static final String AUTHORIZATION_HEADER = "Authorization"; // Имя заголовка HTTP
    private static final String BEARER_PREFIX = "Bearer "; // Префикс токена в заголовке
    private static final String LOGIN_PATH = "/auth/login"; // Путь для логина (без проверки токена)
    private static final String REGISTER_PATH = "/auth/register"; // Путь для регистрации (без проверки токена)

    // Внедрение зависимости через конструктор (благодаря @RequiredArgsConstructor)
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Главный метод фильтра, вызывается для КАЖДОГО HTTP запроса (один раз за запрос)
     * Фильтр проверяет наличие и валидность JWT токена в заголовке Authorization
     */
    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request, // Входящий HTTP запрос (от клиента)
            @NotNull HttpServletResponse response, // Исходящий HTTP ответ (к клиенту)
            @NotNull FilterChain filterChain) // Цепочка фильтров для продолжения обработки
            throws ServletException, IOException {

        // Пробуем получить заголовок Authorization из запроса
        // Optional.ofNullable - если заголовка нет, вернется пустой Optional
        Optional<String> authHeader = Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER));

        // Получаем URI (путь) запроса, например: /users/all, /auth/login
        String requestURI = request.getRequestURI();

        // Проверяем: есть ли заголовок И начинается ли он с "Bearer "
        if (authHeader.isPresent() && authHeader.get().startsWith(BEARER_PREFIX)) {

            // Извлекаем сам токен (отрезаем "Bearer " - первые 7 символов)
            String jwt = authHeader.get().substring(BEARER_PREFIX.length());

            try {
                // Пытаемся валидировать токен
                if (!jwtTokenProvider.validateToken(jwt)) {
                    // Если токен не прошел валидацию (просрочен или недействителен)
                    throw new ExpiredJwtException(null, null, ApiErrorMessage.TOKEN_EXPIRED.getMessage());
                }

                // Извлекаем email пользователя из токена
                Optional<String> emailOpt = Optional.ofNullable(jwtTokenProvider.getUsername(jwt));
                // Извлекаем ID пользователя из токена
                Optional<String> userIdOpt = Optional.ofNullable(jwtTokenProvider.getUserId(jwt));

                // Проверяем, что и email и userId есть в токене
                if (emailOpt.isPresent() && userIdOpt.isPresent()) {
                    // Проверяем, что пользователь еще не аутентифицирован в этом запросе
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {

                        // Получаем роли пользователя из токена и преобразуем их в формат Spring Security
                        // SimpleGrantedAuthority - это стандартный класс Spring для прав доступа
                        List<SimpleGrantedAuthority> authorities = jwtTokenProvider.getRoles(jwt).stream()
                                .map(SimpleGrantedAuthority::new) // Преобразуем строку роли в SimpleGrantedAuthority
                                .collect(Collectors.toList());

                        // Создаем объект аутентификации Spring Security
                        // Он содержит: email (как principal), токен, и права доступа (роли)
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        emailOpt.get(), // principal (обычно username/email)
                                        jwt, // credentials (сами данные аутентификации)
                                        authorities // права доступа (roles)
                                );

                        // Устанавливаем аутентификацию в SecurityContext
                        // Теперь Spring Security знает, что пользователь аутентифицирован
                        // и может проверять его права при доступе к endpoint'ам
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            } catch (ExpiredJwtException e) {
                // Специальная обработка для просроченных токенов
                // Например, на /auth/login можно обновить токен
                handleTokenExpiration(requestURI, jwt, response);
                return; // Прерываем выполнение, ответ уже отправлен
            } catch (SignatureException | MalformedJwtException e) {
                // Токен имеет неправильную подпись или поврежден (возможно, подделан)
                handleSignatureException(response);
                return; // Прерываем выполнение
            } catch (Exception e) {
                // Любые другие непредвиденные ошибки
                handleUnexpectedException(response, e);
                return; // Прерываем выполнение
            }
        }

        // Продолжаем цепочку фильтров:
        // - если не было заголовка Authorization (пользователь не аутентифицирован)
        // - если аутентификация прошла успешно (установили SecurityContext)
        // - если была ошибка аутентификации, мы уже отправили ответ и сделали return
        filterChain.doFilter(request, response);
    }

    /**
     * Обрабатывает ситуацию с просроченным токеном
     * Если запрос на /auth/login или /auth/register - обновляем токен
     * Иначе возвращаем ошибку 401 Unauthorized
     */
    private void handleTokenExpiration(String requestURI, String jwt, HttpServletResponse response) throws IOException {
        if (isAuthEndpoint(requestURI)) {
            // Если это endpoint аутентификации, обновляем токен
            String refreshedToken = jwtTokenProvider.refreshToken(jwt);
            // Отправляем новый токен в заголовке ответа
            response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + refreshedToken);
        } else {
            // Иначе возвращаем ошибку "токен просрочен"
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, ApiErrorMessage.TOKEN_EXPIRED.getMessage());
        }
    }

    /**
     * Обрабатывает ошибку подписи токена (токен подделан или поврежден)
     */
    private void handleSignatureException(HttpServletResponse response) throws IOException {
        sendErrorResponse(response, HttpStatus.UNAUTHORIZED, ApiErrorMessage.INVALID_TOKEN_SIGNATURE.getMessage());
    }

    /**
     * Обрабатывает непредвиденные ошибки
     */
    private void handleUnexpectedException(HttpServletResponse response, Exception e) throws IOException {
        log.error(ApiErrorMessage.ERROR_DURING_JWT_PROCESSING.getMessage(), e);
        sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, ApiErrorMessage.UNEXPECTED_ERROR_OCCURRED.getMessage());
    }

    /**
     * Вспомогательный метод для отправки ошибки клиенту
     */
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value()); // Устанавливаем HTTP статус (401, 500 и т.д.)
        response.getWriter().write(message); // Записываем сообщение об ошибке в тело ответа
    }

    /**
     * Проверяет, является ли запрос на endpoint аутентификации (логин/регистрация)
     * На этих endpoint'ах мы разрешаем обновление просроченного токена
     */
    private boolean isAuthEndpoint(String uri) {
        return uri.equals(LOGIN_PATH) || uri.equals(REGISTER_PATH);
    }

}
