package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.CardProductBenefit;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardProductBenefitResponse {

    private Long id;
    private Long cardProductId;

    private String benefitName;              // 혜택 이름 (예: "편의점 15% 할인")
    private String benefitType;             // 혜택 타입 (예: DISCOUNT, POINT ...)
    private String categoryCode;            // 카테고리 코드 (예: CONVENIENCE_STORE, CAFE ...)

    private BigDecimal discountRate;        // 할인율 (0.15 = 15%)
    private BigDecimal maxDiscountPerMonth; // 월 최대 할인 한도
    private BigDecimal minSpendingForActivation; // 이 혜택 발동 최소 실적 (예: 300_000원)

    private String description;             // 설명
    private Boolean active;                 // 사용 여부

    public static CardProductBenefitResponse from(CardProductBenefit benefit) {
        return CardProductBenefitResponse.builder()
            .id(benefit.getId())
            .cardProductId(
                benefit.getCardProduct() != null ? benefit.getCardProduct().getId() : null
            )
            .benefitName(benefit.getBenefitName())
            .benefitType(
                benefit.getBenefitType() != null ? benefit.getBenefitType().name() : null
            )
            .categoryCode(benefit.getCategoryCode())
            .discountRate(benefit.getDiscountRate())
            .maxDiscountPerMonth(benefit.getMaxDiscountPerMonth())
            .minSpendingForActivation(benefit.getMinSpendingForActivation())
            .description(benefit.getDescription())
            .active(benefit.isActive())
            .build();
    }
}