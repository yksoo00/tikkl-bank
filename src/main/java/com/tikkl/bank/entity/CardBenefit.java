package com.tikkl.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Column(nullable = false, length = 20)
    private BenefitType benefitType;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal discountRate; // 15.00(%처럼 저장) 또는 0.15(%를 비율로 저장) 등 네가 쓰는 형태에 맞추면 됨

    @Column(precision = 19, scale = 2)
    private BigDecimal maxDiscount; // 최대 할인 금액

    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal targetAmount = BigDecimal.ZERO; // 혜택 달성 목표 금액

    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO; // 현재 달성 금액

    @Column(length = 50)
    private String category; // 적용 카테고리 (식비, 교통, 쇼핑 등)

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum BenefitType {
        DISCOUNT,    // 할인
        CASHBACK,    // 캐시백
        POINT,       // 포인트 적립
        MILEAGE      // 마일리지
    }

    /**
     * 어떤 상품 혜택에서 복사된 건지 (추적용)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private CardProductBenefit productBenefit;

    public BigDecimal getAchievementRate() {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentAmount
            .multiply(BigDecimal.valueOf(100))
            .divide(targetAmount, 2, java.math.RoundingMode.DOWN);
    }

    public void addSpending(BigDecimal amount) {
        if (amount == null) {
            return;
        }
        this.currentAmount = this.currentAmount.add(amount);
    }
}