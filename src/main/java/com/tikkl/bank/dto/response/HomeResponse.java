package com.tikkl.bank.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeResponse {

    private SavingsAccountResponse savingsAccount;
    private List<TransactionResponse> recentTransactions;

    public static HomeResponse of(SavingsAccountResponse savings,
        List<TransactionResponse> transactions) {
        return HomeResponse.builder()
            .savingsAccount(savings)
            .recentTransactions(transactions)
            .build();
    }
}