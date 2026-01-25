package com.post_hub.iam_service.model.dto.comment;

import com.post_hub.iam_service.model.dto.post.PostOwnerDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/*
    Отправка данных клиенту (HTTP-ответы)
    Представление полной информации о комментарии
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO implements Serializable {

    private Integer id;
    private String message;
    private PostOwnerDTO owner;
    private Integer postId;
    private LocalDateTime created;
    private LocalDateTime updated;

}
