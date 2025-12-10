CREATE TABLE member
(
    member_id        BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,

    email            VARCHAR(255) NOT NULL,

    roles            VARCHAR(100) NOT NULL,

    nickname         VARCHAR(100) NOT NULL,
    CONSTRAINT uk_member_nickname UNIQUE (nickname),

    created_at       DATETIME     NOT NULL,
    last_modified_at DATETIME     NOT NULL
);