package com.tikkl.bank.dto.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CardPaymentResponse {

    private Long transactionId;
    private BigDecimal paymentAmount;   // 결제 금액
    private BigDecimal benefitAmount;   // 할인 금액
    private BigDecimal savingsAmount;   // 자동저축 금액 (지금은 0)
    private String merchant;
    private String category;
    private BigDecimal cardMonthlyTotal;
    private BigDecimal nextBillingAmount;
}