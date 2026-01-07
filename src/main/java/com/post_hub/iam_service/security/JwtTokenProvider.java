package com.post_hub.iam_service.security;

import com.post_hub.iam_service.model.entity.Role;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.service.model.AuthenticationConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j // Аннотация для автоматического создания логгера
@Component // Аннотация указывает, что этот класс является Spring компонентом (бин)
public class JwtTokenProvider {
    private final SecretKey secretKey; // Секретный ключ для подписи JWT токенов
    private final Long jwtValidityInMilliseconds; // Время жизни токена в миллисекундах

    // Конструктор, куда Spring автоматически подставляет значения из application.properties
    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration:3600000}") long jwtValidityInMilliseconds) {
        this.secretKey = getKey(secret); // Преобразуем строку секрета в SecretKey
        this.jwtValidityInMilliseconds = jwtValidityInMilliseconds; // Сохраняем время жизни токена (по умолчанию 1 час = 3600000 мс)
    }

    // Метод для создания JWT токена для пользователя
    public String generateToken(@NonNull User user) {
        Map<String, Object> claims = new HashMap<>(); // Создаем Map для хранения claims (данных) токена
        claims.put(AuthenticationConstants.USER_ID, user.getId()); // ID пользователя
        claims.put(AuthenticationConstants.USERNAME, user.getUsername()); // Имя пользователя
        claims.put(AuthenticationConstants.USER_EMAIL, user.getEmail()); // Email
        claims.put(AuthenticationConstants.USER_REGISTRATION_STATUS, user.getRegistrationStatus().name()); // Статус регистрации
        claims.put(AuthenticationConstants.LAST_UPDATE, LocalDateTime.now().toString()); // Время последнего обновления

        // Получаем список ролей пользователя и преобразуем в список имен ролей
        List<String> rolesList = user.getRoles().stream()
                .map(Role::getName) // Берем имя каждой роли
                .collect(Collectors.toList()); // Собираем в список
        claims.put(AuthenticationConstants.ROLE, rolesList); // Добавляем роли в claims

        return createToken(claims, user.getEmail()); // Создаем токен с claims и email в качестве subject
    }

    // Метод для обновления токена (создает новый токен с теми же claims)
    public String refreshToken(String token) {
        Claims claims = getAllClaimsFromToken(token); // Получаем все claims из старого токена
        return createToken(claims, claims.getSubject()); // Создаем новый токен с теми же claims
    }

    // Метод для проверки валидности токена
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder() // Парсим токен и проверяем его подпись
                    .setSigningKey(secretKey) // Устанавливаем ключ для проверки подписи
                    .build()
                    .parseClaimsJws(token); // Парсим токен

            return !claims.getBody().getExpiration().before(new Date()); // Проверяем, не истек ли срок действия токена
        } catch (JwtException | IllegalArgumentException e) {
            // Если токен невалиден (подделан, истек и т.д.), возвращаем false
            return false;
        }
    }

    // Метод для получения username из токена
    public String getUsername(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get(AuthenticationConstants.USERNAME, String.class);
    }

    // Метод для получения списка ролей из токена
    public List<String> getRoles(String token) {
        // Достаем роли из claims токена
        return getAllClaimsFromToken(token).get(AuthenticationConstants.ROLE, List.class);
    }

    // Приватный метод для получения всех claims из токена
    private Claims getAllClaimsFromToken(String token) {
        try {
            // Парсим токен и возвращаем его claims (данные)
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // Если токен истек, все равно возвращаем claims (для refresh токена)
            return e.getClaims();
        }
    }

    // Приватный метод для преобразования строки секрета в SecretKey
    private SecretKey getKey(String secretKey64) {
        // Декодируем base64 строку в массив байтов
        byte[] decode64 = Decoders.BASE64.decode(secretKey64);
        // Создаем SecretKey из массива байтов
        return Keys.hmacShaKeyFor(decode64);
    }

    // Приватный метод для создания JWT токена
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // Устанавливаем данные токена
                .setSubject(subject) // Устанавливаем subject (обычно email)
                .setIssuedAt(new Date()) // Время создания токена
                .setExpiration(new Date(System.currentTimeMillis() + jwtValidityInMilliseconds)) // Время истечения
                .signWith(secretKey, SignatureAlgorithm.HS512) // Подписываем токен с алгоритмом HS512
                .compact(); // Собираем токен в строку
    }
}
