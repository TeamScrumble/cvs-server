CREATE TABLE product
(
    product_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    cvs_product_id  BIGINT,
    cvs_target      VARCHAR(50)       NOT NULL,
    title           VARCHAR(255)      NOT NULL,
    img             VARCHAR(500)      NOT NULL,
    price           INT               NOT NULL,
    event           VARCHAR(10)       NOT NULL,
    is_new          TINYINT(1)        NOT NULL DEFAULT 0,
    created_at       DATETIME         NOT NULL,
    last_modified_at DATETIME         NOT NULL
);

ALTER TABLE product
    ADD CONSTRAINT uq_cvs_product UNIQUE (cvs_product_id);