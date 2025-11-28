package com.tikkl.bank.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private Card card;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal savingsAmount = BigDecimal.ZERO; // 저축된 금액

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal balanceAfter = BigDecimal.ZERO; // 거래 후 잔액

    @Column(length = 100)
    private String description; // 거래 내용

    @Column(length = 100)
    private String merchant; // 가맹점 정보

    @Column(length = 50)
    private String category; // 거래 카테고리

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.COMPLETED;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime transactionAt = LocalDateTime.now();

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum TransactionType {
        DEPOSIT,      // 입금
        WITHDRAWAL,   // 출금
        TRANSFER,     // 이체
        PAYMENT,      // 결제
        SAVINGS,      // 저축
        INTEREST      // 이자
    }

    public enum TransactionStatus {
        PENDING,    // 처리중
        COMPLETED,  // 완료
        FAILED,     // 실패
        CANCELLED   // 취소
    }
}
