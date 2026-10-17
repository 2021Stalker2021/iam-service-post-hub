package com.post_hub.iam_service.security.handler;

import com.post_hub.iam_service.model.constants.ApiErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class AccessRestrictionHandler implements AccessDeniedHandler {

    // этот метод перехватывает 403 ошибки при недостатке прав, и отправляет сообщение что, у нас нет доступа
    @Override
    @SneakyThrows
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(ApiErrorMessage.HAVE_NO_ACCESS.getMessage());
    }

}
