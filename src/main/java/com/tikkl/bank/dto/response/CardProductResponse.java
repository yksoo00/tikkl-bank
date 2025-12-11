package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.CardProduct;
import com.tikkl.bank.entity.CardType;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardProductResponse {

    private Long id;
    private String name;
    private String company;
    private CardType cardType;
    private BigDecimal annualFee;
    private String description;
    private boolean active;
    private String imageUrl;
    private String summaryBenefits;

    private List<CardProductBenefitDto> benefits;

    public static CardProductResponse from(CardProduct product) {
        return CardProductResponse.builder()
            .id(product.getId())
            .name(product.getName())
            .company(product.getCompany())
            .annualFee(product.getAnnualFee())
            .description(product.getDescription())
            .imageUrl(product.getImageUrl())
            .summaryBenefits(product.getSummaryBenefits())
            .build();
    }
}