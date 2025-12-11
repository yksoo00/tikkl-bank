package com.tikkl.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class SavingsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(nullable = false, unique = true, length = 30)
    private String accountNumber;

    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalSaved = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalInterest = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    private FinancialProduct financialProduct;

    // 현재 적용 이자율
    @Column(precision = 10, scale = 4)
    private BigDecimal interestRate;

    private LocalDate maturityDate;

    private Long remainingDays;

    private LocalDate lastInterestAppliedDate;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;
}