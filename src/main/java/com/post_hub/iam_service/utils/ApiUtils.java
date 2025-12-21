package com.post_hub.iam_service.utils;

import com.post_hub.iam_service.model.constants.ApiConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import java.util.UUID;

public class ApiUtils {

    public static String getMethodName() {
        try {
            return Thread.currentThread().getStackTrace()[2].getMethodName();
            // [1] getMethodName() метод, который вызвал текущий
        } catch (Exception cause) {
            return ApiConstants.UNDEFINED;
        }
    }

    public static Cookie createAuthCookie(String value) {
        Cookie authorizationCookie = new Cookie(HttpHeaders.AUTHORIZATION, value); // Имя куки и значение
        authorizationCookie.setHttpOnly(true); // Защита от XSS-атак
        authorizationCookie.setSecure(true); // Только по HTTPS
        authorizationCookie.setPath("/"); // Доступно для всех путей
        authorizationCookie.setMaxAge(300); // Время жизни
        return authorizationCookie;
    }

    // Метод генерирует рандомный uuid и убирает дефисы
    public static String generateUuidWithoutDash() {
        return UUID.randomUUID().toString().replace(ApiConstants.DASH, StringUtils.EMPTY);
    }
}
