package com.tikkl.bank.service;

import com.tikkl.bank.dto.request.CardRequest;
import com.tikkl.bank.dto.response.CardResponse;
import com.tikkl.bank.entity.Card;
import com.tikkl.bank.entity.Member;
import com.tikkl.bank.exception.CustomException;
import com.tikkl.bank.exception.ErrorCode;
import com.tikkl.bank.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final MemberService memberService;

    public List<CardResponse> getCards(Long memberId) {
        Member member = memberService.findMemberById(memberId);
        return cardRepository.findByMemberAndIsActiveTrue(member).stream()
                .map(CardResponse::from)
                .collect(Collectors.toList());
    }

    public CardResponse getCard(Long memberId, Long cardId) {
        Member member = memberService.findMemberById(memberId);
        Card card = findCardById(cardId);
        
        validateCardOwner(card, member);
        return CardResponse.from(card);
    }

    @Transactional
    public CardResponse registerCard(Long memberId, CardRequest request) {
        Member member = memberService.findMemberById(memberId);

        Card.CardType cardType;
        try {
            cardType = Card.CardType.valueOf(request.getCardType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CardException(ErrorCode.INVALID_CARD_TYPE);
        }

        // 카드번호 마스킹 처리
        String maskedCardNumber = maskCardNumber(request.getCardNumber());

        Card card = Card.builder()
                .member(member)
                .cardNumber(maskedCardNumber)
                .cardName(request.getCardName())
                .cardCompany(request.getCardCompany())
                .cardType(cardType)
                .expiryDate(request.getExpiryDate())
                .bonusSavingsRatio(request.getBonusSavingsRatio() != null ? 
                        request.getBonusSavingsRatio() : BigDecimal.ZERO)
                .build();

        Card savedCard = cardRepository.save(card);
        return CardResponse.from(savedCard);
    }

    @Transactional
    public CardResponse updateBonusSavingsRatio(Long memberId, Long cardId, BigDecimal ratio) {
        Member member = memberService.findMemberById(memberId);
        Card card = findCardById(cardId);
        
        validateCardOwner(card, member);
        card.setBonusSavingsRatio(ratio);
        
        return CardResponse.from(card);
    }

    @Transactional
    public void deactivateCard(Long memberId, Long cardId) {
        Member member = memberService.findMemberById(memberId);
        Card card = findCardById(cardId);
        
        validateCardOwner(card, member);
        card.setIsActive(false);
    }

    private Card findCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new CardException(ErrorCode.CARD_NOT_FOUND));
    }

    private void validateCardOwner(Card card, Member member) {
        if (!card.getMember().getId().equals(member.getId())) {
            throw new CardException(ErrorCode.FORBIDDEN);
        }
    }

    private String maskCardNumber(String cardNumber) {
        String digits = cardNumber.replaceAll("[^0-9]", "");
        if (digits.length() < 4) {
            return "****";
        }
        return "**** **** **** " + digits.substring(digits.length() - 4);
    }

    public static class CardException extends CustomException {
        public CardException(ErrorCode errorCode) {
            super(errorCode);
        }
    }
}
