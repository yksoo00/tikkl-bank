package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.CardBenefit;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class CardBenefitResponse {

    private Long id;
    private String benefitName;
    private String benefitDescription;
    private String benefitType;
    private BigDecimal discountRate;
    private BigDecimal maxDiscount;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private BigDecimal achievementRate;
    private String category;
    private Boolean isActive;

    public static CardBenefitResponse from(CardBenefit benefit) {
        return CardBenefitResponse.builder()
                .id(benefit.getId())
                .benefitName(benefit.getBenefitName())
                .benefitDescription(benefit.getBenefitDescription())
                .benefitType(benefit.getBenefitType().name())
                .discountRate(benefit.getDiscountRate())
                .maxDiscount(benefit.getMaxDiscount())
                .targetAmount(benefit.getTargetAmount())
                .currentAmount(benefit.getCurrentAmount())
                .achievementRate(benefit.getAchievementRate())
                .category(benefit.getCategory())
                .isActive(benefit.getIsActive())
                .build();
    }
}
