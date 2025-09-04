package com.post_hub.iam_service.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false) // это поле означает что оно обязательно для заполнения
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false, updatable = false) // updatable = false - указывает что это поле нельзя будет изменять
    // если запись уже создана, время уже никогда не поменяется
    private LocalDateTime created = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updated = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer likes = 0;
}
