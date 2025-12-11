package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.CardBenefit;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CardBenefitResponse {

    private Long id;
    private String category;
    private BigDecimal targetAmount;
    private BigDecimal discountRate;
    private BigDecimal maxDiscount;
    private BigDecimal currentAmount;

    public static CardBenefitResponse from(CardBenefit b) {
        return CardBenefitResponse.builder()
            .id(b.getId())
            .category(b.getCategory())
            .targetAmount(b.getTargetAmount())
            .discountRate(b.getDiscountRate())
            .maxDiscount(b.getMaxDiscount())
            .currentAmount(b.getCurrentAmount())
            .build();
    }
}