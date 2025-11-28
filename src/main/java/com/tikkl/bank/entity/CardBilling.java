package com.tikkl.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "card_billing")
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
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(nullable = false)
    private Integer billingYear; // 청구 년도

    @Column(nullable = false)
    private Integer billingMonth; // 청구 월

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO; // 총 청구 금액

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal benefitAmount = BigDecimal.ZERO; // 총 혜택 금액

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal savedAmount = BigDecimal.ZERO; // 총 저축 금액

    @Column(nullable = false)
    @Builder.Default
    private Integer transactionCount = 0; // 거래 건수

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BillingStatus status = BillingStatus.PENDING;

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

    public enum BillingStatus {
        PENDING,    // 청구 예정
        BILLED,     // 청구됨
        PAID        // 결제 완료
    }

    public YearMonth getBillingYearMonth() {
        return YearMonth.of(billingYear, billingMonth);
    }
}
