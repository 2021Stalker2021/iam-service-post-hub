package com.post_hub.iam_service.model.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/*
    Представление информации о владельце в составе других DTO
    Ограничение передаваемых данных о пользователе
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostOwnerDTO implements Serializable {

    private Integer id;
    private String username;
    private String email;
}
