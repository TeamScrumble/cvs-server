-- 리뷰 평가 항목
INSERT INTO review_aspect (title)
VALUES
    ('품질이 어떠셨나요?'),
    ('가성비가 어떠셨나요?'),
    ('재구매 의사가 있으신가요?');

-- 리뷰 평가 항목 옵션
INSERT INTO review_aspect_option (aspect_id, option_text, display_order)
VALUES
    -- attribute_id = 1 (품질)
    (1, '최고에요', 1),
    (1, '괜찮아요', 2),
    (1, '별로에요', 3),
    -- attribute_id = 2 (가성비)
    (2, '최고에요', 1),
    (2, '그냥 그래', 2),
    (2, '별로에요', 3),
    -- attribute_id = 3 (재구매 의사)
    (3, '완전 있어요', 1),
    (3, '모르겠어요', 2),
    (3, '전혀 없어요', 3);