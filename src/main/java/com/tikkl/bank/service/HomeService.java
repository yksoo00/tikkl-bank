package com.tikkl.bank.service;

import com.tikkl.bank.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final MemberService memberService;
    private final SavingsService savingsService;
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final CardService cardService;

    public HomeResponse getHomeData(Long memberId) {
        SavingsAccountResponse savingsAccount = savingsService.getSavingsAccount(memberId);
        List<TransactionResponse> recentTransactions = transactionService.getRecentTransactions(memberId);
        
        return HomeResponse.of(savingsAccount, recentTransactions);
    }

    public MyPageResponse getMyPageData(Long memberId) {
        MemberResponse member = memberService.getMember(memberId);
        SavingsAccountResponse savingsAccount = savingsService.getSavingsAccount(memberId);
        List<AccountResponse> accounts = accountService.getAccounts(memberId);
        List<CardResponse> cards = cardService.getCards(memberId);

        return MyPageResponse.of(member, savingsAccount, accounts, cards);
    }
}
