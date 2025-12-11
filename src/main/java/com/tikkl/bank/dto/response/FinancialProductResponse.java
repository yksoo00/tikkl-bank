package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.FinancialProduct;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinancialProductResponse {

    private Long id;
    private String productName;
    private String productCode;
    private String productType;
    private BigDecimal interestRate;
    private BigDecimal maxInterestRate;
    private String provider;

    public static FinancialProductResponse from(FinancialProduct product) {
        return FinancialProductResponse.builder()
            .id(product.getId())
            .productName(product.getProductName())
            .productCode(product.getProductCode())
            .productType(product.getProductType().name())
            .interestRate(product.getInterestRate())
            .maxInterestRate(product.getMaxInterestRate())
            .provider(product.getProvider())
            .build();
    }
}