package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.CardBilling;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class CardBillingResponse {

    private Long id;
    private Long cardId;
    private Integer billingYear;
    private Integer billingMonth;
    private BigDecimal totalAmount;
    private BigDecimal benefitAmount;
    private BigDecimal savedAmount;
    private Integer transactionCount;
    private String status;

    public static CardBillingResponse from(CardBilling billing) {
        return CardBillingResponse.builder()
                .id(billing.getId())
                .cardId(billing.getCard().getId())
                .billingYear(billing.getBillingYear())
                .billingMonth(billing.getBillingMonth())
                .totalAmount(billing.getTotalAmount())
                .benefitAmount(billing.getBenefitAmount())
                .savedAmount(billing.getSavedAmount())
                .transactionCount(billing.getTransactionCount())
                .status(billing.getStatus().name())
                .build();
    }
}
