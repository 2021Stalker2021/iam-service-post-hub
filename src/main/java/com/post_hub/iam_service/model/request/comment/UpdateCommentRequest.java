package com.post_hub.iam_service.model.request.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
    Получение данных от клиента (HTTP-запросы)
    Валидация входных данных перед обработкой
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentRequest {

    // Проверяет: что значение не равно null
    @NotNull(message = "Post ID cannot be null")
    private Integer postId;

    // Проверяет: что значение не null, не пустая строка и не состоит из пробелов
    @NotBlank(message = "Content cannot be empty")
    private String message;

}
