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
public class CardBilling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;

    @Column(nullable = false)
    private Integer billingYear;

    @Column(nullable = false)
    private Integer billingMonth;

    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;   // 할인 후 청구액

    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal benefitAmount = BigDecimal.ZERO; // 할인 혜택 총합

    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal savedAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false)
    private Integer transactionCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BillingStatus status;

    public enum BillingStatus {
        PENDING,
        PAID,
        FAILED
    }
}