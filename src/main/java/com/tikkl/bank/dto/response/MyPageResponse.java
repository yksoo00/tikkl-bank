package com.tikkl.bank.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyPageResponse {

    private MemberResponse member;
    private SavingsAccountResponse savingsAccount;
    private List<AccountResponse> accounts;
    private List<CardResponse> cards;

    public static MyPageResponse of(
        MemberResponse member,
        SavingsAccountResponse savings,
        List<AccountResponse> accounts,
        List<CardResponse> cards
    ) {
        return MyPageResponse.builder()
            .member(member)
            .savingsAccount(savings)
            .accounts(accounts)
            .cards(cards)
            .build();
    }
}