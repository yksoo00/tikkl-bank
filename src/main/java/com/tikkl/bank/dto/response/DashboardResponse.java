package com.tikkl.bank.dto.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardResponse {

    // ===== 티클 통장(저축 계좌) =====
    /** 티클 통장 잔액 */
    private BigDecimal tikklBalance;

    /** 지금까지 넣은 총 저축금액 */
    private BigDecimal tikklTotalSaved;

    /** 지금까지 받은 누적 이자 */
    private BigDecimal tikklTotalInterest;

    /** 이번 달에 받은 이자 합계 */
    private BigDecimal tikklInterestThisMonth;

    /** 연결된 금융상품 이름 (ex. 티끌 모아 태산 적금) */
    private String tikklProductName;

    /** 적용 이자율(%) */
    private BigDecimal tikklInterestRate;

    /** 만기까지 남은 일수 */
    private Long tikklRemainingDays;

    // ===== 카드 / 청구 =====
    /** 다음 달 청구 예정 카드 금액(모든 카드 합산) */
    private BigDecimal nextMonthBillingAmount;

    /** 이번 달 카드 사용액(모든 카드 currentMonthSpending 합산) */
    private BigDecimal cardSpendingThisMonth;

    /** 이번 달 받은 카드 혜택 금액(모든 카드 청구 기준) */
    private BigDecimal cardBenefitThisMonth;

    // ===== 다음 혜택 구간 정보 =====
    /** 다음 혜택 구간 라벨 (예: "30만원 구간") */
    private String nextBenefitStageLabel;

    /** 다음 혜택 구간 목표 금액 */
    private BigDecimal nextBenefitTargetAmount;

    /** 다음 혜택 구간까지 남은 금액 */
    private BigDecimal nextBenefitRemainingAmount;
}