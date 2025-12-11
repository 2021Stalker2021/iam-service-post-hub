package com.post_hub.iam_service.service;

import com.post_hub.iam_service.model.entity.RefreshToken;
import com.post_hub.iam_service.model.entity.User;

public interface RefreshTokenService {

    RefreshToken generateOrUpdateRefreshToken(User user);

    RefreshToken validateAndRefreshToken(String refreshToken);
}
