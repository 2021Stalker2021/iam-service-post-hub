package com.post_hub.iam_service.security.validation;

import com.post_hub.iam_service.model.request.user.RegistrationUserRequest;
import com.post_hub.iam_service.utils.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegistrationUserRequest> {

    @Override
    public boolean isValid(RegistrationUserRequest request, ConstraintValidatorContext context) {
        return request.getPassword().equals(request.getConfirmPassword());
    }
}
