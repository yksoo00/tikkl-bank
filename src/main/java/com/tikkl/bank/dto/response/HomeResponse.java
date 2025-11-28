package com.tikkl.bank.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class HomeResponse {

    private BigDecimal currentSavingsBalance;      // 현재 저축 잔액
    private BigDecimal expectedInterest;           // 예상 이자
    private Long remainingDays;                    // 만기까지 잔여일
    private BigDecimal interestRate;               // 이자율
    private List<TransactionResponse> recentTransactions; // 최근 거래 내역

    public static HomeResponse of(
            SavingsAccountResponse savingsAccount,
            List<TransactionResponse> recentTransactions) {
        return HomeResponse.builder()
                .currentSavingsBalance(savingsAccount.getBalance())
                .expectedInterest(savingsAccount.getExpectedInterest())
                .remainingDays(savingsAccount.getRemainingDays())
                .interestRate(savingsAccount.getInterestRate())
                .recentTransactions(recentTransactions)
                .build();
    }
}
