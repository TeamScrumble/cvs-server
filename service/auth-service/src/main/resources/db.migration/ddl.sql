CREATE TABLE IF NOT EXISTS auth
(
    auth_id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider         VARCHAR(50)  NOT NULL,
    provider_id      VARCHAR(255) NOT NULL,
    member_id        BIGINT       NOT NULL,
    created_at       DATETIME     NOT NULL,
    last_modified_at DATETIME     NOT NULL,

    UNIQUE KEY uk_auth_provider_provider_id (provider, provider_id)
);

CREATE TABLE IF NOT EXISTS email_auth
(
    email_auth_id    BIGINT       NOT NULL AUTO_INCREMENT,
    email            VARCHAR(255) NOT NULL UNIQUE,
    encoded_password VARCHAR(255) NOT NULL,

    created_at       DATETIME(6)  NOT NULL,
    last_modified_at DATETIME(6)  NOT NULL,

    PRIMARY KEY (email_auth_id),

    UNIQUE KEY uk_auth_email (email)
);