package com.tikkl.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class FinancialProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String productName;

    @Column(nullable = false, length = 50, unique = true)
    private String productCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductType productType;

    @Column(nullable = false, length = 50)
    private String provider;

    // 이자율 (예: 0.025 같은 값 저장하려면 scale 4 정도)
    @Column(precision = 10, scale = 4)
    private BigDecimal interestRate;

    @Column(precision = 10, scale = 4)
    private BigDecimal maxInterestRate;

    private Integer minTerm;
    private Integer maxTerm;

    @Column(precision = 19, scale = 2)
    private BigDecimal minAmount;

    @Column(precision = 19, scale = 2)
    private BigDecimal maxAmount;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String terms;

    @Column(length = 500)
    private String benefits;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    public enum ProductType {
        SAVINGS,
        FIXED,
        FUND
    }
}