package com.tikkl.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    // 마스터카드
    @ManyToOne(fetch = FetchType.LAZY)
    private CardProduct cardProduct;

    // 카드 번호 관련
    @Column(length = 30)
    private String maskedCardNumber;

    @Column(length = 10)
    private String lastFourDigits;

    @Column(length = 50)
    private String nickname;

    // 결제일 (예: 15일, 25일)
    private Integer billingDay;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account paymentAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    private SavingsAccount linkedSavingsAccount;

    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentMonthSpending = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalBenefitReceived = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;
}