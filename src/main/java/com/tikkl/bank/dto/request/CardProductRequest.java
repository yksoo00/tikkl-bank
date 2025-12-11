package com.tikkl.bank.dto.request;

import com.tikkl.bank.entity.CardType;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class CardProductRequest {

    private String name;
    private String company;
    private CardType cardType;
    private BigDecimal annualFee;
    private String description;
    private String imageUrl;
    private String summaryBenefits;

    private List<CardProductBenefitRequest> benefits;
}