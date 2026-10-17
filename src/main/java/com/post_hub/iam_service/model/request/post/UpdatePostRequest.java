package com.post_hub.iam_service.model.request.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostRequest implements Serializable {

    @NotBlank(message = "Title cannot be empty") // не null, не пустая, не только из пробелов
    private String title;
    @NotBlank(message = "Content cannot be empty")
    private String content;
    @NotNull(message = "Specify the number of likes")
    private String likes;
}
