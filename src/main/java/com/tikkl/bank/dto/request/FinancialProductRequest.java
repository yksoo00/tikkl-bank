package com.tikkl.bank.dto.request;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class FinancialProductRequest {

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
}