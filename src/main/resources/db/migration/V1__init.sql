CREATE TABLE users
(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(80) NOT NULL,
    email VARCHAR(50) UNIQUE,
    created TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    registration_status VARCHAR(30) NOT NULL,
    last_login TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE posts
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGSERIAL NOT NULL,
    title   VARCHAR(255) NOT NULL,
    content TEXT         NOT NULL,
    created TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false,
    likes   INTEGER      NOT NULL DEFAULT 0,
    created_by VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (title)
);

INSERT INTO users (username, password, email, created, updated, registration_status, last_login, deleted) VALUES
                        ('first_user', '$2a$10$hinjZ1dDfRr2wO4ZCSJRNOl4a3CzdLzx5K/bCdowxMMcLjE6zLg0m', 'first_user@gmail.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, false),
                        ('second_user', '$2a$10$qtIX985Dui2qs0me/yjRKuCz7obiSdwiD.VArgKfcEsFMkCooD3Qa', 'second_user@gmail.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, false),
                        ('third_user', '$2a$10$hTmyfkxY39.NlO/6JW3BtuykHLc/0/mMplYlh63YeHxfrebx0DgSy', 'third_user@gmail.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, false);

INSERT INTO posts (user_id, title, content, created, updated, deleted, likes) VALUES
                        (1, 'First post', 'This is content of the first post', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 6),
                        (2, 'Second post', 'This is content of the second post', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 3);