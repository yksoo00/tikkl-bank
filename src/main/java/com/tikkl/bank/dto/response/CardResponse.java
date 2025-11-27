package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.Card;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class CardResponse {

    private Long id;
    private String cardNumber;
    private String cardName;
    private String cardCompany;
    private String cardType;
    private LocalDate expiryDate;
    private Boolean isActive;
    private BigDecimal bonusSavingsRatio;
    private String benefits;

    public static CardResponse from(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .cardNumber(card.getCardNumber())
                .cardName(card.getCardName())
                .cardCompany(card.getCardCompany())
                .cardType(card.getCardType().name())
                .expiryDate(card.getExpiryDate())
                .isActive(card.getIsActive())
                .bonusSavingsRatio(card.getBonusSavingsRatio())
                .benefits(card.getBenefits())
                .build();
    }
}
