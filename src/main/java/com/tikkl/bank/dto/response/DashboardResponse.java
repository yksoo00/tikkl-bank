package com.tikkl.bank.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class DashboardResponse {

    // 저축 정보
    private BigDecimal totalSavingsBalance; // 총 저축 잔액
    private BigDecimal totalInterestEarned; // 누적 이자
    private BigDecimal expectedMonthlyInterest; // 이번 달 예상 이자
    private Long remainingDays; // 만기까지 남은 일

    // 카드 정보
    private BigDecimal totalCardSpendingThisMonth; // 이번 달 총 카드 사용액
    private BigDecimal totalBenefitThisMonth; // 이번 달 받은 혜택
    private BigDecimal nextMonthBilling; // 다음 달 총 청구 예정액

    // 계좌 정보
    private BigDecimal totalAccountBalance; // 총 계좌 잔액

    // 최근 거래
    private List<TransactionResponse> recentTransactions;
}
