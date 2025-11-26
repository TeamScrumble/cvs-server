CREATE TABLE auth
(
    auth_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider        VARCHAR(50)  NOT NULL,
    provider_id     VARCHAR(255) NOT NULL,
    member_id       BIGINT       NOT NULL,
    created_at      DATETIME     NOT NULL,
    last_modified_at DATETIME    NOT NULL,

    UNIQUE KEY uk_auth_provider_provider_id (provider, provider_id)
);