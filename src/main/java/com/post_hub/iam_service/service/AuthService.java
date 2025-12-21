package com.post_hub.iam_service.service;

import com.post_hub.iam_service.model.request.user.LoginRequest;
import com.post_hub.iam_service.model.dto.user.UserProfileDTO;
import com.post_hub.iam_service.model.response.IamResponse;

public interface AuthService {

    IamResponse<UserProfileDTO> login(LoginRequest request);

    IamResponse<UserProfileDTO> refreshAccessToken(String refreshToken);
}
