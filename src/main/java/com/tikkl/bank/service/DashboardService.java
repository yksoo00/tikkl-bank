package com.tikkl.bank.service;

import com.tikkl.bank.dto.response.DashboardResponse;
import com.tikkl.bank.dto.response.TransactionResponse;
import com.tikkl.bank.entity.*;
import com.tikkl.bank.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final MemberService memberService;
    private final SavingsAccountRepository savingsAccountRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final CardBillingRepository cardBillingRepository;
    private final TransactionRepository transactionRepository;

    public DashboardResponse getDashboard(Long memberId) {
        Member member = memberService.findMemberById(memberId);

        // 저축 계좌 정보
        BigDecimal totalSavingsBalance = BigDecimal.ZERO;
        BigDecimal totalInterestEarned = BigDecimal.ZERO;
        BigDecimal expectedMonthlyInterest = BigDecimal.ZERO;
        Long remainingDays = 0L;

        SavingsAccount savingsAccount = savingsAccountRepository.findByMember(member).orElse(null);
        if (savingsAccount != null) {
            totalSavingsBalance = savingsAccount.getBalance();
            totalInterestEarned = savingsAccount.getTotalInterest();
            remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), savingsAccount.getMaturityDate());
            if (remainingDays < 0) remainingDays = 0L;

            // 예상 월 이자 계산 (단리 기준)
            expectedMonthlyInterest = totalSavingsBalance
                    .multiply(savingsAccount.getInterestRate())
                    .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        }

        // 계좌 총 잔액
        BigDecimal totalAccountBalance = accountRepository.findByMemberAndIsActiveTrue(member).stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 카드 정보
        List<Card> cards = cardRepository.findByMemberAndIsActiveTrue(member);
        BigDecimal totalCardSpendingThisMonth = cards.stream()
                .map(Card::getCurrentMonthSpending)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 이번 달 혜택
        YearMonth currentMonth = YearMonth.now();
        BigDecimal totalBenefitThisMonth = BigDecimal.ZERO;
        for (Card card : cards) {
            BigDecimal benefit = cardBillingRepository
                    .findByCardAndBillingYearAndBillingMonth(card, currentMonth.getYear(), currentMonth.getMonthValue())
                    .map(CardBilling::getBenefitAmount)
                    .orElse(BigDecimal.ZERO);
            totalBenefitThisMonth = totalBenefitThisMonth.add(benefit);
        }

        // 다음 달 청구 예정
        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        BigDecimal nextMonthBilling = BigDecimal.ZERO;
        for (Card card : cards) {
            BigDecimal billing = cardBillingRepository
                    .findByCardAndBillingYearAndBillingMonth(card, nextMonth.getYear(), nextMonth.getMonthValue())
                    .map(CardBilling::getTotalAmount)
                    .orElse(BigDecimal.ZERO);
            nextMonthBilling = nextMonthBilling.add(billing);
        }

        // 최근 거래
        List<TransactionResponse> recentTransactions = transactionRepository
                .findTop10ByMemberOrderByTransactionAtDesc(member).stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalSavingsBalance(totalSavingsBalance)
                .totalInterestEarned(totalInterestEarned)
                .expectedMonthlyInterest(expectedMonthlyInterest)
                .remainingDays(remainingDays)
                .totalCardSpendingThisMonth(totalCardSpendingThisMonth)
                .totalBenefitThisMonth(totalBenefitThisMonth)
                .nextMonthBilling(nextMonthBilling)
                .totalAccountBalance(totalAccountBalance)
                .recentTransactions(recentTransactions)
                .build();
    }
}
