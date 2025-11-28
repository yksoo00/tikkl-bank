package com.tikkl.bank.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class CardDetailResponse {

    private CardResponse card;
    private List<CardBenefitResponse> benefits;
    private CardBillingResponse nextBilling; // 다음 달 청구 예정
    private BigDecimal monthlySpendingTarget;
    private BigDecimal currentMonthSpending;
    private BigDecimal spendingAchievementRate; // 목표 달성률
    private BigDecimal totalBenefitThisMonth; // 이번 달 받은 혜택
    private BigDecimal totalBenefitReceived; // 누적 혜택
    private List<TransactionResponse> recentTransactions;
}
