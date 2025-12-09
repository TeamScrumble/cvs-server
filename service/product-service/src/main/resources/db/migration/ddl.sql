-- 상품 리뷰 테이블
CREATE TABLE IF NOT EXISTS review (
    review_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id  BIGINT              NOT NULL,
    member_id   BIGINT              NOT NULL,
    rating      TINYINT UNSIGNED    NOT NULL,
    content     VARCHAR(500)        NOT NULL,
    is_deleted  TINYINT(1)          NOT NULL DEFAULT 0,
    created_at       DATETIME       NOT NULL,
    last_modified_at DATETIME       NOT NULL
);

-- 상품 리뷰 평가 카테고리 테이블 (품질/가성비/재구매의사)
CREATE TABLE IF NOT EXISTS review_aspect (
    aspect_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(100) NOT NULL
);

-- 평가 카테고리별 옵션 테이블 (최고에요/괜찮아요/별로에요)
CREATE TABLE IF NOT EXISTS review_aspect_option (
    option_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    aspect_id    BIGINT NOT NULL,
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