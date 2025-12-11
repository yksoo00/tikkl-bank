package com.tikkl.bank.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CardDetailResponse {

    private CardResponse card;
    private List<CardBenefitResponse> benefits;
    private CardBillingResponse nextBilling;
    private BigDecimal monthlySpendingTarget;
    private BigDecimal currentMonthSpending;
    private BigDecimal spendingAchievementRate;
    private BigDecimal totalBenefitThisMonth;
    private BigDecimal totalBenefitReceived;
    private List<TransactionResponse> recentTransactions;
}