CREATE TABLE product
(
    product_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(255)      NOT NULL,
    img             VARCHAR(500)      NOT NULL,
    price           INT               NOT NULL,
    event           VARCHAR(10)       NOT NULL,
    is_new          TINYINT(1)        NOT NULL DEFAULT 0,
    created_at       DATETIME         NOT NULL,
    last_modified_at DATETIME         NOT NULL
);