package com.tikkl.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "card_benefits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(nullable = false, length = 100)
    private String benefitName; // 혜택 이름

    @Column(length = 500)
    private String benefitDescription; // 혜택 설명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BenefitType benefitType;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal discountRate; // 할인율 (%)

    @Column(precision = 19, scale = 2)
    private BigDecimal maxDiscount; // 최대 할인 금액

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal targetAmount = BigDecimal.ZERO; // 혜택 달성 목표 금액

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal currentAmount = BigDecimal.ZERO; // 현재 달성 금액

    @Column(length = 50)
    private String category; // 적용 카테고리 (식비, 교통, 쇼핑 등)

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum BenefitType {
        DISCOUNT,    // 할인
        CASHBACK,    // 캐시백
        POINT,       // 포인트 적립
        MILEAGE      // 마일리지
    }

    // 달성률 계산
    public BigDecimal getAchievementRate() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }
        return currentAmount.multiply(BigDecimal.valueOf(100))
                .divide(targetAmount, 2, java.math.RoundingMode.HALF_UP);
    }
}
