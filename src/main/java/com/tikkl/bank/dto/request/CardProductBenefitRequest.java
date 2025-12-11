package com.tikkl.bank.dto.request;

import com.tikkl.bank.entity.BenefitType;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CardProductBenefitRequest {

    private String benefitName;
    private BenefitType benefitType;

    /**
     * 카테고리 코드(예: CAFE, MART, RESTAURANT)
     */
    private String categoryCode;

    private BigDecimal discountRate;
    private BigDecimal maxDiscountPerMonth;
    private BigDecimal minSpendingForActivation;
    private String description;
}