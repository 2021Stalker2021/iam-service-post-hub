package com.post_hub.iam_service.service.impl;

import com.post_hub.iam_service.mapper.UserMapper;
import com.post_hub.iam_service.model.constants.ApiErrorMessage;
import com.post_hub.iam_service.model.dto.user.UserDto;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.model.exception.DataExistException;
import com.post_hub.iam_service.model.exception.NotFoundException;
import com.post_hub.iam_service.model.request.user.NewUserRequest;
import com.post_hub.iam_service.model.response.IamResponse;
import com.post_hub.iam_service.repository.UserRepository;
import com.post_hub.iam_service.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public IamResponse<UserDto> getById(@NotNull Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

        UserDto userDTO = userMapper.toUserDTO(user);
        return IamResponse.createSuccessful(userDTO);
    }

    @Override
    public IamResponse<UserDto> createUser(@NotNull NewUserRequest newUserRequest) {
        if (userRepository.existsByUsername(newUserRequest.getUsername())) {
            throw new DataExistException(ApiErrorMessage.USERNAME_ALREADY_EXISTS.getMessage(newUserRequest.getUsername()));
        }
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new DataExistException(ApiErrorMessage.EMAIL_ALREADY_EXISTS.getMessage(newUserRequest.getEmail()));
        }

        User user = userMapper.createUser(newUserRequest);
        User savedUser = userRepository.save(user);
        UserDto userDto = userMapper.toUserDTO(savedUser);

        return IamResponse.createSuccessful(userDto);
    }
}
