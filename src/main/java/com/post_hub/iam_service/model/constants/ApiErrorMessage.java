package com.post_hub.iam_service.model.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApiErrorMessage {

    POST_NOT_FOUND_BY_ID("POST with ID: %s was not found");

    private final String message;

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
