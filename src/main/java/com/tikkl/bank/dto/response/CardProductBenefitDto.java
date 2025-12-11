package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.*;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardProductBenefitDto {

    private Long id;
    private String benefitName;
    private BenefitType benefitType;
    private String categoryCode;
    private BigDecimal discountRate;
    private BigDecimal maxDiscountPerMonth;
    private BigDecimal minSpendingForActivation;
    private String description;
    private boolean active;

    public static CardProductBenefitDto from(CardProductBenefit benefit) {
        return CardProductBenefitDto.builder()
            .id(benefit.getId())
            .benefitName(benefit.getBenefitName())
            .benefitType(benefit.getBenefitType())
            .categoryCode(benefit.getCategoryCode())
            .discountRate(benefit.getDiscountRate())
            .maxDiscountPerMonth(benefit.getMaxDiscountPerMonth())
            .minSpendingForActivation(benefit.getMinSpendingForActivation())
            .description(benefit.getDescription())
            .active(benefit.isActive())
            .build();
    }
}