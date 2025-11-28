package com.tikkl.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "savings_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false, unique = true, length = 30)
    private String accountNumber; // 티끌 전용 통장 계좌번호

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO; // 현재 저축 잔액

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalSaved = BigDecimal.ZERO; // 누적 저축 금액

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalInterest = BigDecimal.ZERO; // 누적 이자 금액

    @Column(nullable = false, precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal interestRate = BigDecimal.valueOf(0.035); // 이자율 (3.5%)

    @Column(nullable = false)
    @Builder.Default
    private LocalDate maturityDate = LocalDate.now().plusMonths(12); // 만기일

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
}
