package com.tikkl.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
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
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 20)
    private String cardNumber; // 마스킹된 카드 번호

    @Column(nullable = false, length = 50)
    private String cardName;

    @Column(nullable = false, length = 50)
    private String cardCompany;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CardType cardType = CardType.CREDIT;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal bonusSavingsRatio = BigDecimal.ZERO; // 보너스 티끌 비율

    @Column(length = 500)
    private String benefits; // 카드 혜택 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_savings_account_id")
    private SavingsAccount linkedSavingsAccount; // 카드 사용시 저축될 계좌

    @Column(precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal monthlySpendingTarget = BigDecimal.ZERO; // 월 사용 목표 금액

    @Column(precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal currentMonthSpending = BigDecimal.ZERO; // 이번 달 사용 금액

    @Column(precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalBenefitReceived = BigDecimal.ZERO; // 총 받은 혜택 금액

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

    public enum CardType {
        CREDIT,  // 신용카드
        DEBIT,   // 체크카드
        PREPAID  // 선불카드
    }
}
