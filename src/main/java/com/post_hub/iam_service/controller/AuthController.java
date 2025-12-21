package com.post_hub.iam_service.controller;

import com.post_hub.iam_service.model.constants.ApiLogMessage;
import com.post_hub.iam_service.model.request.user.LoginRequest;
import com.post_hub.iam_service.model.dto.user.UserProfileDTO;
import com.post_hub.iam_service.model.response.IamResponse;
import com.post_hub.iam_service.service.AuthService;
import com.post_hub.iam_service.utils.ApiUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<UserProfileDTO> result = authService.login(request);
        Cookie authorizationCookie = ApiUtils.createAuthCookie(result.getPayload().getToken());
        response.addCookie(authorizationCookie);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/refresh/token")
    public ResponseEntity<IamResponse<UserProfileDTO>> refreshToken(
            @RequestParam(name = "token") String refreshToken,
            HttpServletResponse response) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        // вернёт refresh и access токен в ответе IamResponse<UserProfileDTO>
        IamResponse<UserProfileDTO> result = authService.refreshAccessToken(refreshToken);
        // установка в куки access токен доступа
        Cookie authorizationCookie = ApiUtils.createAuthCookie(result.getPayload().getToken());
        response.addCookie(authorizationCookie); // установка токена в HTTP-ответ

        return ResponseEntity.ok(result);
    }
}
