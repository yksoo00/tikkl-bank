package com.tikkl.bank.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class MyPageResponse {

    private MemberResponse member;
    private SavingsAccountResponse savingsAccount;
    private List<AccountResponse> accounts;
    private List<CardResponse> cards;
    private BigDecimal savingsRatio;
    private Boolean autoSavingsEnabled;

    public static MyPageResponse of(
            MemberResponse member,
            SavingsAccountResponse savingsAccount,
            List<AccountResponse> accounts,
            List<CardResponse> cards) {
        return MyPageResponse.builder()
                .member(member)
                .savingsAccount(savingsAccount)
                .accounts(accounts)
                .cards(cards)
                .savingsRatio(member.getSavingsRatio())
                .autoSavingsEnabled(member.getAutoSavingsEnabled())
                .build();
    }
}
