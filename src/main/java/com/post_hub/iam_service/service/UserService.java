package com.post_hub.iam_service.service;

import com.post_hub.iam_service.model.dto.user.UserDto;
import com.post_hub.iam_service.model.response.IamResponse;
import jakarta.validation.constraints.NotNull;

public interface UserService {

    IamResponse<UserDto> getById(@NotNull Long userId);

}
