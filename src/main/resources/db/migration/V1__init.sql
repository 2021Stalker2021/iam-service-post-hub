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
    title   VARCHAR(255) NOT NULL,
    content TEXT         NOT NULL,
    created TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false,
    likes   INTEGER      NOT NULL DEFAULT 0,
    UNIQUE (title)
);

INSERT INTO users (username, password, email, created, updated, registration_status, last_login, deleted) VALUES
                        ('first_user', 'password1', 'first_user@gmail.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, false),
                        ('second_user', 'password2', 'second_user@gmail.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, false),
                        ('third_user', 'password3', 'third_user@gmail.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, false);

INSERT INTO posts (title, content, created, updated, deleted, likes) VALUES
                        ('First post', 'This is content of the first post', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 6),
                        ('Second post', 'This is content of the second post', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, 3);