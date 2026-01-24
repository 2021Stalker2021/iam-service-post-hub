package com.post_hub.iam_service.security.validation;

import com.post_hub.iam_service.model.constants.ApiErrorMessage;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.model.exception.DataExistException;
import com.post_hub.iam_service.model.exception.InvalidPasswordException;
import com.post_hub.iam_service.model.exception.NotFoundException;
import com.post_hub.iam_service.repository.UserRepository;
import com.post_hub.iam_service.service.model.IamServiceUserRole;
import com.post_hub.iam_service.utils.ApiUtils;
import com.post_hub.iam_service.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

@Component
@RequiredArgsConstructor
public class AccessValidator {
    private final UserRepository userRepository;

    public void validateNewUser(String username, String email, String password, String confirmPassword) {
        userRepository.findByUsername(username).ifPresent(existingUser -> {
            throw new DataExistException(ApiErrorMessage.USERNAME_ALREADY_EXISTS.getMessage(username));
        });

        // выбрасываем исключение если такой email уже существует
        userRepository.findByEmail(email).ifPresent(existingUser -> {
            throw new DataExistException(ApiErrorMessage.EMAIL_ALREADY_EXISTS.getMessage(email));
        });

        if (!password.equals(confirmPassword)) {
            throw new InvalidPasswordException(ApiErrorMessage.MISMATCH_PASSWORDS.getMessage());
        }

        if (PasswordUtils.isNotValidPassword(password)) {
            throw new InvalidPasswordException(ApiErrorMessage.INVALID_PASSWORD.getMessage());
        }
    }

    public boolean isAdminOrSuperAdmin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USERNAME_NOT_FOUND.getMessage()));

        return user.getRoles().stream()
                .map(role -> IamServiceUserRole.fromName(role.getName()))
                .anyMatch(role -> role == IamServiceUserRole.ADMIN || role == IamServiceUserRole.SUPER_ADMIN);
        /*
            Метод anyMatch() проверяет, удовлетворяет ли хотя бы один элемент в потоке заданному условию (предикату).
         */
    }

    @SneakyThrows
    public void validateAdminOrOwnerAccess(String ownerUsername, String createdBy) {
        String currentUsername = ApiUtils.getCurrentUsername();

        if (!currentUsername.equals(ownerUsername) &&
                !currentUsername.equals(createdBy) &&
                !isAdminOrSuperAdmin(currentUsername)) {
            throw new AccessDeniedException(ApiErrorMessage.HAVE_NO_ACCESS.getMessage());
        }
    }
}
