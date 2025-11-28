package com.tikkl.bank.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class CardPaymentResponse {

    private Long transactionId;
    private BigDecimal paymentAmount;
    private BigDecimal savingsAmount;
    private BigDecimal benefitAmount;
    private String merchant;
    private String category;
    private BigDecimal cardMonthlyTotal; // 카드 월 사용 총액
    private BigDecimal nextBillingAmount; // 다음 청구 예정액
}
