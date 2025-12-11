package com.tikkl.bank.service;

import com.tikkl.bank.dto.response.DashboardResponse;
import com.tikkl.bank.entity.Card;
import com.tikkl.bank.entity.CardBenefit;
import com.tikkl.bank.entity.CardBilling;
import com.tikkl.bank.entity.InterestHistory;
import com.tikkl.bank.entity.Member;
import com.tikkl.bank.entity.SavingsAccount;
import com.tikkl.bank.repository.CardBenefitRepository;
import com.tikkl.bank.repository.CardBillingRepository;
import com.tikkl.bank.repository.CardRepository;
import com.tikkl.bank.repository.InterestHistoryRepository;
import com.tikkl.bank.repository.SavingsAccountRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final MemberService memberService;
    private final SavingsAccountRepository savingsAccountRepository;
    private final InterestHistoryRepository interestHistoryRepository;
    private final CardRepository cardRepository;
    private final CardBenefitRepository cardBenefitRepository;
    private final CardBillingRepository cardBillingRepository;

    public DashboardResponse getDashboard(Long memberId) {

        // ===== 0. 회원 조회 =====
        Member member = memberService.findMemberById(memberId);

        // ===== 1. 티클 통장(저축 계좌) =====
        SavingsAccount savings = savingsAccountRepository.findByMember(member).orElse(null);

        BigDecimal tikklBalance = BigDecimal.ZERO;
        BigDecimal tikklTotalSaved = BigDecimal.ZERO;
        BigDecimal tikklTotalInterest = BigDecimal.ZERO;
        BigDecimal tikklInterestThisMonth = BigDecimal.ZERO;
        String tikklProductName = null;
        BigDecimal tikklInterestRate = null;
        Long tikklRemainingDays = null;

        if (savings != null) {
            tikklBalance = nvl(savings.getBalance());
            tikklTotalSaved = nvl(savings.getTotalSaved());
            tikklTotalInterest = nvl(savings.getTotalInterest());

            if (savings.getFinancialProduct() != null) {
                tikklProductName = savings.getFinancialProduct().getProductName();
                tikklInterestRate = nvl(savings.getFinancialProduct().getInterestRate());
            } else {
                tikklInterestRate = nvl(savings.getInterestRate());
            }

            if (savings.getMaturityDate() != null) {
                long days = ChronoUnit.DAYS.between(LocalDate.now(), savings.getMaturityDate());
                tikklRemainingDays = Math.max(days, 0L);
            }

            // 이번 달 이자 : InterestHistory 기반으로 계산
            YearMonth thisMonth = YearMonth.now();
            LocalDate start = thisMonth.atDay(1);
            LocalDate end = thisMonth.atEndOfMonth();

            List<InterestHistory> histories =
                interestHistoryRepository.findBySavingsAccountAndInterestDateBetween(
                    savings,
                    start,
                    end
                );

            tikklInterestThisMonth = histories.stream()
                .map(h -> nvl(h.getInterestAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // ===== 2. 카드 / 청구 =====
        List<Card> cards = cardRepository.findByMemberAndIsActiveTrue(member);

        // 이번 달 카드 사용액 : 각 카드의 currentMonthSpending 합산
        BigDecimal cardSpendingThisMonth = cards.stream()
            .map(c -> nvl(c.getCurrentMonthSpending()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 다음 달 청구 예정 금액
        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        BigDecimal nextMonthBillingAmount = cards.stream()
            .map(card -> cardBillingRepository
                .findByCardAndBillingYearAndBillingMonth(
                    card,
                    nextMonth.getYear(),
                    nextMonth.getMonthValue()
                )
                .map(CardBilling::getTotalAmount)
                .orElse(BigDecimal.ZERO)
            )
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 이번 달 카드 혜택 금액 (CardBilling의 benefitAmount 합산)
        YearMonth currentMonth = YearMonth.now();
        BigDecimal cardBenefitThisMonth = cards.stream()
            .map(card -> cardBillingRepository
                .findByCardAndBillingYearAndBillingMonth(
                    card,
                    currentMonth.getYear(),
                    currentMonth.getMonthValue()
                )
                .map(CardBilling::getBenefitAmount)
                .orElse(BigDecimal.ZERO)
            )
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ===== 3. 다음 혜택 구간까지 남은 금액 =====
        // 모든 카드의 활성화된 혜택을 모아서, (target - current) > 0 인 것 중 gap이 가장 작은 것 선택
        List<CardBenefit> allBenefits = cards.stream()
            .flatMap(card -> cardBenefitRepository.findByCardAndIsActiveTrue(card).stream())
            .toList();

        String nextBenefitStageLabel = null;
        BigDecimal nextBenefitTargetAmount = BigDecimal.ZERO;
        BigDecimal nextBenefitRemainingAmount = BigDecimal.ZERO;

        BenefitGap bestGap = allBenefits.stream()
            .filter(b -> b.getTargetAmount() != null) // 목표 금액 있는 혜택만
            .map(b -> new BenefitGap(
                b,
                nvl(b.getTargetAmount()).subtract(nvl(b.getCurrentAmount()))
            ))
            .filter(bg -> bg.gap.compareTo(BigDecimal.ZERO) > 0) // 아직 달성 못한 구간
            .min(Comparator.comparing(bg -> bg.gap))
            .orElse(null);

        if (bestGap != null) {
            CardBenefit b = bestGap.benefit;
            BigDecimal target = nvl(b.getTargetAmount());
            nextBenefitTargetAmount = target;
            nextBenefitRemainingAmount = bestGap.gap;

            // 예: 300000 -> "30만원 구간"
            BigDecimal man = new BigDecimal("10000");
            BigDecimal manUnit = target.divide(man, 0, java.math.RoundingMode.DOWN);
            nextBenefitStageLabel = manUnit.toPlainString() + "만원 구간";
        }

        return DashboardResponse.builder()
            .tikklBalance(tikklBalance)
            .tikklTotalSaved(tikklTotalSaved)
            .tikklTotalInterest(tikklTotalInterest)
            .tikklInterestThisMonth(tikklInterestThisMonth)
            .tikklProductName(tikklProductName)
            .tikklInterestRate(tikklInterestRate)
            .tikklRemainingDays(tikklRemainingDays)
            .nextMonthBillingAmount(nextMonthBillingAmount)
            .cardSpendingThisMonth(cardSpendingThisMonth)
            .cardBenefitThisMonth(cardBenefitThisMonth)
            .nextBenefitStageLabel(nextBenefitStageLabel)
            .nextBenefitTargetAmount(nextBenefitTargetAmount)
            .nextBenefitRemainingAmount(nextBenefitRemainingAmount)
            .build();
    }

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static class BenefitGap {
        private final CardBenefit benefit;
        private final BigDecimal gap;

        private BenefitGap(CardBenefit benefit, BigDecimal gap) {
            this.benefit = benefit;
            this.gap = gap;
        }
    }
}