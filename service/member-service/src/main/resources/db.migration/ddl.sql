CREATE TABLE IF NOT EXISTS member
(
    member_id        BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,

    email            VARCHAR(255) NOT NULL,

    roles            VARCHAR(100) NOT NULL,

    nickname         VARCHAR(100) NOT NULL,
    CONSTRAINT uk_member_nickname UNIQUE (nickname),

    profile_image    VARCHAR(512) NOT NULL,

    created_at       DATETIME     NOT NULL,
    last_modified_at DATETIME     NOT NULL
);

CREATE TABLE agreement
(
    agreement_id     BIGINT       NOT NULL AUTO_INCREMENT,
    type             VARCHAR(50)  NOT NULL,
    required         BOOLEAN      NOT NULL,
    label            VARCHAR(100) NOT NULL,
    document_url     VARCHAR(255) NOT NULL,
    version          VARCHAR(50)  NOT NULL,
    is_active        BOOLEAN      NOT NULL,
    created_at       DATETIME     NOT NULL,
    last_modified_at DATETIME     NOT NULL,

    PRIMARY KEY (agreement_id),
    INDEX idx_agreement_type (type),
    INDEX idx_agreement_active (is_active)
);

CREATE TABLE member_agreement
(
    member_agreement_id BIGINT   NOT NULL AUTO_INCREMENT,
    agreed              BOOLEAN  NOT NULL,
    agreed_at           DATETIME NOT NULL,
    member_id           BIGINT   NOT NULL,
    agreement_id        BIGINT   NOT NULL,
    created_at          DATETIME NOT NULL,
    last_modified_at    DATETIME NOT NULL,

    PRIMARY KEY (member_agreement_id),
    UNIQUE KEY uk_member_agreement (member_id, agreement_id),
    INDEX idx_member_agreement_member (member_id),
    INDEX idx_member_agreement_agreement (agreement_id)
);