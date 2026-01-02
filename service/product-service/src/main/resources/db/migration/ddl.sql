-- 상품 테이블
CREATE TABLE IF NOT EXISTS product
(
    product_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    cvs_product_id  BIGINT            NOT NULL,
    cvs_target      VARCHAR(50)       NOT NULL,
    title           VARCHAR(255)      NOT NULL,
    img             VARCHAR(500)      NOT NULL,
    price           INT               NOT NULL,
    event           VARCHAR(10)       NOT NULL,
    is_new          TINYINT(1)        NOT NULL DEFAULT 0,
    like_count      INT               NOT NULL DEFAULT 0,
    created_at       DATETIME         NOT NULL,
    last_modified_at DATETIME         NOT NULL
);

ALTER TABLE product
    ADD CONSTRAINT uq_cvs_product UNIQUE (cvs_product_id);

CREATE TABLE IF NOT EXISTS product_like (
    product_like_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id      BIGINT NOT NULL,
    member_id       BIGINT NOT NULL,
    created_at      DATETIME NOT NULL,
    last_modified_at DATETIME NOT NULL,

    CONSTRAINT uq_product_like
      UNIQUE (product_id, member_id),

    CONSTRAINT fk_product_like_product
      FOREIGN KEY (product_id) REFERENCES product(product_id)
)

-- 상품 리뷰 테이블
CREATE TABLE IF NOT EXISTS review (
    review_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id  BIGINT              NOT NULL,
    member_id   BIGINT              NOT NULL,
    rating      TINYINT UNSIGNED    NOT NULL,
    content     VARCHAR(500)        NOT NULL,
    is_deleted  TINYINT(1)          NOT NULL DEFAULT 0,
    is_receipt  TINYINT(1)          NOT NULL DEFAULT 0, -- 영수증 인증 여부
    created_at       DATETIME       NOT NULL,
    last_modified_at DATETIME       NOT NULL
);
ALTER TABLE review
  ADD COLUMN like_count BIGINT NOT NULL DEFAULT 0;

-- 상품 리뷰 평가 카테고리 테이블 (품질/가성비/재구매의사)
CREATE TABLE IF NOT EXISTS review_aspect (
    aspect_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(100) NOT NULL,
    question    VARCHAR(255) NOT NULL
);

-- 평가 카테고리별 옵션 테이블 (최고에요/괜찮아요/별로에요)
CREATE TABLE IF NOT EXISTS review_aspect_option (
    option_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    aspect_id       BIGINT NOT NULL,
    option_text     VARCHAR(255) NOT NULL,
    display_order   INT NOT NULL
);

-- 상품 리뷰 평가 결과 테이블 (품질/가성비/재구매의사)
CREATE TABLE IF NOT EXISTS review_aspect_score (
    score_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id   BIGINT NOT NULL,
    option_id   BIGINT NOT NULL,
    created_at       DATETIME       NOT NULL,
    last_modified_at DATETIME       NOT NULL
);

-- 상품 리뷰 도움돼요 테이블
CREATE TABLE IF NOT EXISTS review_like (
    review_like_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id       BIGINT NOT NULL,
    member_id       BIGINT NOT NULL,

    UNIQUE KEY uk_review_like (review_id, member_id)
);

-- 상품 리뷰 이미지 테이블
CREATE TABLE IF NOT EXISTS review_img (
    review_img_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id      BIGINT NOT NULL,
    img_url        VARCHAR(500) NOT NULL,
    display_order  INT NOT NULL,
    is_deleted     TINYINT(1)       NOT NULL DEFAULT 0,
    created_at       DATETIME       NOT NULL,
    last_modified_at DATETIME       NOT NULL
);

-- review.product_id -> product.product_id
ALTER TABLE review
    ADD CONSTRAINT fk_review_product
        FOREIGN KEY (product_id) REFERENCES product(product_id);

-- review_aspect_option.aspect_id -> review_aspect.aspect_id
ALTER TABLE review_aspect_option
    ADD CONSTRAINT fk_review_aspect_option_aspect
        FOREIGN KEY (aspect_id) REFERENCES review_aspect(aspect_id);

-- review_aspect_score.review_id -> review.review_id
ALTER TABLE review_aspect_score
    ADD CONSTRAINT fk_review_aspect_score_review
        FOREIGN KEY (review_id) REFERENCES review(review_id);

-- review_aspect_score.option_id -> review_aspect_option.option_id
ALTER TABLE review_aspect_score
    ADD CONSTRAINT fk_review_aspect_score_option
        FOREIGN KEY (option_id) REFERENCES review_aspect_option(option_id);

-- review_like.review_id -> review.review_id
ALTER TABLE review_like
    ADD CONSTRAINT fk_review_like_review
        FOREIGN KEY (review_id) REFERENCES review(review_id);

-- review_img.review_id -> review.review_id
ALTER TABLE review_img
    ADD CONSTRAINT fk_review_img_review
        FOREIGN KEY (review_id) REFERENCES review(review_id);


-- 상품 리뷰 신고 테이블
CREATE TABLE IF NOT EXISTS review_report (
    review_report_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '리뷰 신고 ID',
    review_id BIGINT NOT NULL COMMENT '신고 대상 리뷰 ID',
    member_id BIGINT NOT NULL COMMENT '신고자 회원 ID',
    reason_code VARCHAR(30) NOT NULL COMMENT '신고 사유 코드',
    content  VARCHAR(500) COMMENT '신고 내용',
    status   VARCHAR(30) DEFAULT 'PENDING' COMMENT '신고 처리 상태',
    processed_at     DATETIME       NOT NULL COMMENT '처리 일시(처리 완료 시점)',
    created_at       DATETIME       NOT NULL,
    last_modified_at DATETIME       NOT NULL
);