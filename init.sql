use tikklbank;
create database tikklbank;
INSERT INTO financial_product (product_name,
                               product_code,
                               product_type,
                               provider,
                               interest_rate,
                               max_interest_rate,
                               min_term,
                               max_term,
                               min_amount,
                               max_amount,
                               description,
                               terms,
                               benefits,
                               is_active)
VALUES
-- 자유적금
('티끌 자유적금', 'FP001', 'SAVINGS', 'TIKKL BANK',
 0.025, 0.03, 1, 36, 10000, 10000000,
 '사용자가 자유롭게 금액을 넣고 출금할 수 있는 적금 상품',
 '자유롭게 입출금 가능. 월 납입 한도 없음.',
 '적립식 이자 지급, 자동이체 가능',
 true),

-- 정기적금
('티끌 정기적금', 'FP002', 'SAVINGS', 'TIKKL BANK',
 0.03, 0.035, 6, 36, 50000, 20000000,
 '6개월 이상 가입 가능한 정기적금 상품',
 '중도해지 시 기본이율의 50% 지급',
 '목표금액 달성 시 추가금리 제공',
 true),

-- 예금
('TIKKL 예금', 'FP003', 'SAVINGS', 'TIKKL BANK',
 0.02, 0.025, 1, 24, 100000, 50000000,
 '안정적인 금리를 제공하는 예금 상품',
 '만기 자동갱신 가능',
 '신규 고객 우대금리 제공',
 true);


INSERT INTO card_product (name,
                          company,
                          card_type,
                          annual_fee,
                          description,
                          image_url,
                          summary_benefits,
                          active)
VALUES
-- 티끌 플래티넘 카드
('TIKKL PLATINUM CARD', 'TIKKL CARD', 'CREDIT',
 30000,
 '생활필수영역 10~15% 혜택 제공',
 'https://cdn.tikkl.com/card/platinum.png',
 '편의점, 카페, 배달, 스트리밍 10~15% 할인',
 true),

-- 티끌 생활비 카드
('TIKKL EVERYDAY CARD', 'TIKKL CARD', 'CREDIT',
 10000,
 '카드 실적 기반 구간별 생활 할인 제공',
 'https://cdn.tikkl.com/card/everyday.png',
 '실적 구간별 생활비 최대 20% 할인',
 true);


INSERT INTO card_product_benefit (card_product_id,
                                  benefit_name,
                                  benefit_type,
                                  category_code,
                                  discount_rate,
                                  max_discount_per_month,
                                  min_spending_for_activation,
                                  description,
                                  active)
VALUES (1, '편의점 15% 할인', 'DISCOUNT', 'CONVENIENCE',
        0.15, 10000, 300000, '월 실적 30만원 이상 시 적용', true),

       (1, '카페 10% 할인', 'DISCOUNT', 'CAFE',
        0.10, 8000, 300000, '월 실적 30만원 이상 시 적용', true),

       (1, '배달앱 10% 할인', 'DISCOUNT', 'DELIVERY',
        0.10, 8000, 300000, '월 실적 30만원 이상 시 적용', true);