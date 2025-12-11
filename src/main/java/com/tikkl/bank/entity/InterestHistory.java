package com.tikkl.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "interest_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterestHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "savings_account_id", nullable = false)
    private SavingsAccount savingsAccount;

    @Column(nullable = false)
    private LocalDate interestDate;

    /**
     * 해당 날짜에 적립된 이자 금액
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal interestAmount;

    /**
     * 이자 반영 후 잔액
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;
}