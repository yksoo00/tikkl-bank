package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.CardBilling;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CardBillingResponse {

    private Long id;
    private Integer year;
    private Integer month;
    private BigDecimal totalAmount;
    private BigDecimal benefitAmount;
    private BigDecimal savedAmount;
    private Integer transactionCount;
    private String status;

    public static CardBillingResponse from(CardBilling billing) {
        return CardBillingResponse.builder()
            .id(billing.getId())
            .year(billing.getBillingYear())
            .month(billing.getBillingMonth())
            .totalAmount(billing.getTotalAmount())
            .benefitAmount(billing.getBenefitAmount())
            .savedAmount(billing.getSavedAmount())
            .transactionCount(billing.getTransactionCount())
            .status(billing.getStatus().name())
            .build();
    }
}