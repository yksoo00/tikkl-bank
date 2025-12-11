package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.Card;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CardResponse {

    private Long id;
    private String cardName;      // 별칭 or 마스터 카드 이름
    private String cardNumber;    // 마스킹된 번호
    private String company;       // 카드사 (마스터 기반)
    private BigDecimal currentMonthSpending;
    private BigDecimal totalBenefitReceived;
    private Long cardProductId;

    public static CardResponse from(Card card) {
        return CardResponse.builder()
            .id(card.getId())
            .cardName(
                card.getNickname() != null && !card.getNickname().isBlank()
                    ? card.getNickname()
                    : card.getCardProduct().getName()
            )
            .cardNumber(card.getMaskedCardNumber())
            .company(card.getCardProduct().getCompany())
            .currentMonthSpending(card.getCurrentMonthSpending())
            .totalBenefitReceived(card.getTotalBenefitReceived())
            .cardProductId(card.getCardProduct().getId())
            .build();
    }
}