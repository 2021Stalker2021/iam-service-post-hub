package com.post_hub.iam_service.model.entity;

import com.post_hub.iam_service.model.enums.RegistrationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    public static final String ID_FIELD = "id";
    public static final String USERNAME_NAME_FIELD = "username";
    public static final String EMAIL_NAME_FIELD = "email";
    public static final String DELETED_FIELD = "deleted";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String username;

    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String password;

    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false)
    private LocalDateTime created = LocalDateTime.now(); // при создании будет сразу сохранятся время

    @Column(nullable = false)
    private LocalDateTime updated = LocalDateTime.now(); // при создании будет сразу сохранятся время

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(nullable = false)
    private Boolean deleted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", nullable = false)
    private RegistrationStatus registrationStatus;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles", // связующая таблица
            joinColumns = @JoinColumn(name = "user_id"), // Столбец, который ссылается на текущую сущность (User)
            inverseJoinColumns = @JoinColumn(name = "role_id") // Столбец, который ссылается на связанную сущность (Role)
    )
    private Collection<Role> roles;
}
