package com.tikkl.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_products")
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

    @Column(nullable = false, length = 50)
    private String productCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    @Column(nullable = false, length = 50)
    private String provider; // 금융사

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal interestRate; // 금리

    @Column(precision = 5, scale = 4)
    private BigDecimal maxInterestRate; // 최대 금리

    @Column
    private Integer minTerm; // 최소 기간 (개월)

    @Column
    private Integer maxTerm; // 최대 기간 (개월)

    @Column(precision = 19, scale = 2)
    private BigDecimal minAmount; // 최소 금액

    @Column(precision = 19, scale = 2)
    private BigDecimal maxAmount; // 최대 금액

    @Column(length = 1000)
    private String description;

    @Column(length = 2000)
    private String terms; // 약관

    @Column(length = 500)
    private String benefits; // 혜택

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ProductType {
        SAVINGS,     // 적금
        DEPOSIT,     // 예금
        FUND,        // 펀드
        INSURANCE    // 보험
    }
}
