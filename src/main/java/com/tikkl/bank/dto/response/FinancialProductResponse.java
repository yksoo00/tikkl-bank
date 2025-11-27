package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.FinancialProduct;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class FinancialProductResponse {

    private Long id;
    private String productName;
    private String productCode;
    private String productType;
    private String provider;
    private BigDecimal interestRate;
    private BigDecimal maxInterestRate;
    private Integer minTerm;
    private Integer maxTerm;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String description;
    private String terms;
    private String benefits;

    public static FinancialProductResponse from(FinancialProduct product) {
        return FinancialProductResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .productCode(product.getProductCode())
                .productType(product.getProductType().name())
                .provider(product.getProvider())
                .interestRate(product.getInterestRate())
                .maxInterestRate(product.getMaxInterestRate())
                .minTerm(product.getMinTerm())
                .maxTerm(product.getMaxTerm())
                .minAmount(product.getMinAmount())
                .maxAmount(product.getMaxAmount())
                .description(product.getDescription())
                .terms(product.getTerms())
                .benefits(product.getBenefits())
                .build();
    }
}
