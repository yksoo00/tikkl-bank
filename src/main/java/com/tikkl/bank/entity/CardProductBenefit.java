package com.tikkl.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardProductBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private CardProduct cardProduct;

    @Column(nullable = false, length = 100)
    private String benefitName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BenefitType benefitType;

    @Column(length = 50)
    private String categoryCode;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountRate;           // 0.10 = 10%

    @Column(precision = 19, scale = 2)
    private BigDecimal maxDiscountPerMonth;    // 월 최대 할인 한도

    @Column(precision = 19, scale = 2)
    private BigDecimal minSpendingForActivation; // 티어 조건 (예: 300000)

    @Column(length = 500)
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}