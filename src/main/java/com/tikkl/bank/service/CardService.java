package com.tikkl.bank.service;

import com.tikkl.bank.dto.request.*;
import com.tikkl.bank.dto.response.*;
import com.tikkl.bank.entity.*;
import com.tikkl.bank.exception.CustomException;
import com.tikkl.bank.exception.ErrorCode;
import com.tikkl.bank.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final CardBenefitRepository cardBenefitRepository;
    private final CardBillingRepository cardBillingRepository;
    private final TransactionRepository transactionRepository;
    private final SavingsAccountRepository savingsAccountRepository;
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
                .monthlySpendingTarget(request.getMonthlySpendingTarget() != null ?
                        request.getMonthlySpendingTarget() : BigDecimal.ZERO)
                .benefits(request.getBenefits())
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

    @Transactional
    public CardResponse linkSavingsAccount(Long memberId, Long cardId, LinkSavingsAccountRequest request) {
        Member member = memberService.findMemberById(memberId);
        Card card = findCardById(cardId);
        
        validateCardOwner(card, member);

        SavingsAccount savingsAccount = savingsAccountRepository.findById(request.getSavingsAccountId())
                .orElseThrow(() -> new SavingsService.SavingsException(ErrorCode.SAVINGS_ACCOUNT_NOT_FOUND));

        card.setLinkedSavingsAccount(savingsAccount);
        if (request.getMonthlySpendingTarget() != null) {
            card.setMonthlySpendingTarget(request.getMonthlySpendingTarget());
        }

        return CardResponse.from(card);
    }

    @Transactional
    public CardPaymentResponse processPayment(Long memberId, Long cardId, CardPaymentRequest request) {
        Member member = memberService.findMemberById(memberId);
        Card card = findCardById(cardId);
        
        validateCardOwner(card, member);

        BigDecimal paymentAmount = request.getAmount();
        BigDecimal savingsAmount = BigDecimal.ZERO;
        BigDecimal benefitAmount = BigDecimal.ZERO;

        // 혜택 계산
        List<CardBenefit> benefits = cardBenefitRepository.findByCardAndIsActiveTrue(card);
        for (CardBenefit benefit : benefits) {
            if (benefit.getCategory() == null || benefit.getCategory().equals(request.getCategory())) {
                BigDecimal discount = paymentAmount.multiply(benefit.getDiscountRate())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                if (benefit.getMaxDiscount() != null && discount.compareTo(benefit.getMaxDiscount()) > 0) {
                    discount = benefit.getMaxDiscount();
                }
                benefitAmount = benefitAmount.add(discount);

                // 혜택 달성 금액 업데이트
                benefit.setCurrentAmount(benefit.getCurrentAmount().add(paymentAmount));
            }
        }

        // 저축 금액 계산 (기본 저축 비율 + 카드 보너스 비율)
        BigDecimal totalSavingsRatio = member.getSavingsRatio().add(card.getBonusSavingsRatio());
        savingsAmount = paymentAmount.multiply(totalSavingsRatio)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // 저축 계좌에 금액 추가
        if (card.getLinkedSavingsAccount() != null && savingsAmount.compareTo(BigDecimal.ZERO) > 0) {
            SavingsAccount savingsAccount = card.getLinkedSavingsAccount();
            savingsAccount.setBalance(savingsAccount.getBalance().add(savingsAmount));
            savingsAccount.setTotalSaved(savingsAccount.getTotalSaved().add(savingsAmount));

            // 저축 거래 내역 생성
            Transaction savingsTransaction = Transaction.builder()
                    .member(member)
                    .card(card)
                    .transactionType(Transaction.TransactionType.SAVINGS)
                    .amount(savingsAmount)
                    .balanceAfter(savingsAccount.getBalance())
                    .description("카드 결제 저축")
                    .merchant(request.getMerchant())
                    .category(request.getCategory())
                    .status(Transaction.TransactionStatus.COMPLETED)
                    .build();
            transactionRepository.save(savingsTransaction);
        }

        // 카드 월 사용액 업데이트
        card.setCurrentMonthSpending(card.getCurrentMonthSpending().add(paymentAmount));
        card.setTotalBenefitReceived(card.getTotalBenefitReceived().add(benefitAmount));

        // 결제 거래 내역 생성
        Transaction transaction = Transaction.builder()
                .member(member)
                .card(card)
                .transactionType(Transaction.TransactionType.PAYMENT)
                .amount(paymentAmount)
                .savingsAmount(savingsAmount)
                .description(request.getDescription())
                .merchant(request.getMerchant())
                .category(request.getCategory())
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();
        Transaction savedTransaction = transactionRepository.save(transaction);

        // 월별 청구 업데이트
        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        CardBilling billing = cardBillingRepository
                .findByCardAndBillingYearAndBillingMonth(card, nextMonth.getYear(), nextMonth.getMonthValue())
                .orElseGet(() -> CardBilling.builder()
                        .card(card)
                        .billingYear(nextMonth.getYear())
                        .billingMonth(nextMonth.getMonthValue())
                        .build());
        
        billing.setTotalAmount(billing.getTotalAmount().add(paymentAmount));
        billing.setBenefitAmount(billing.getBenefitAmount().add(benefitAmount));
        billing.setSavedAmount(billing.getSavedAmount().add(savingsAmount));
        billing.setTransactionCount(billing.getTransactionCount() + 1);
        cardBillingRepository.save(billing);

        return CardPaymentResponse.builder()
                .transactionId(savedTransaction.getId())
                .paymentAmount(paymentAmount)
                .savingsAmount(savingsAmount)
                .benefitAmount(benefitAmount)
                .merchant(request.getMerchant())
                .category(request.getCategory())
                .cardMonthlyTotal(card.getCurrentMonthSpending())
                .nextBillingAmount(billing.getTotalAmount())
                .build();
    }

    @Transactional
    public CardBenefitResponse addBenefit(Long memberId, Long cardId, CardBenefitRequest request) {
        Member member = memberService.findMemberById(memberId);
        Card card = findCardById(cardId);
        
        validateCardOwner(card, member);

        CardBenefit.BenefitType benefitType;
        try {
            benefitType = CardBenefit.BenefitType.valueOf(request.getBenefitType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CardException(ErrorCode.INVALID_BENEFIT_TYPE);
        }

        CardBenefit benefit = CardBenefit.builder()
                .card(card)
                .benefitName(request.getBenefitName())
                .benefitDescription(request.getBenefitDescription())
                .benefitType(benefitType)
                .discountRate(request.getDiscountRate())
                .maxDiscount(request.getMaxDiscount())
                .targetAmount(request.getTargetAmount() != null ? request.getTargetAmount() : BigDecimal.ZERO)
                .category(request.getCategory())
                .build();

        CardBenefit savedBenefit = cardBenefitRepository.save(benefit);
        return CardBenefitResponse.from(savedBenefit);
    }

    public List<CardBenefitResponse> getBenefits(Long memberId, Long cardId) {
        Member member = memberService.findMemberById(memberId);
        Card card = findCardById(cardId);
        
        validateCardOwner(card, member);

        return cardBenefitRepository.findByCardAndIsActiveTrue(card).stream()
                .map(CardBenefitResponse::from)
                .collect(Collectors.toList());
    }

    public CardDetailResponse getCardDetail(Long memberId, Long cardId) {
        Member member = memberService.findMemberById(memberId);
        Card card = findCardById(cardId);
        
        validateCardOwner(card, member);

        List<CardBenefitResponse> benefits = cardBenefitRepository.findByCardAndIsActiveTrue(card).stream()
                .map(CardBenefitResponse::from)
                .collect(Collectors.toList());

        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        CardBillingResponse nextBilling = cardBillingRepository
                .findByCardAndBillingYearAndBillingMonth(card, nextMonth.getYear(), nextMonth.getMonthValue())
                .map(CardBillingResponse::from)
                .orElse(null);

        // 이번 달 혜택 계산
        YearMonth currentMonth = YearMonth.now();
        BigDecimal totalBenefitThisMonth = cardBillingRepository
                .findByCardAndBillingYearAndBillingMonth(card, currentMonth.getYear(), currentMonth.getMonthValue())
                .map(CardBilling::getBenefitAmount)
                .orElse(BigDecimal.ZERO);

        BigDecimal spendingRate = BigDecimal.ZERO;
        if (card.getMonthlySpendingTarget().compareTo(BigDecimal.ZERO) > 0) {
            spendingRate = card.getCurrentMonthSpending()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(card.getMonthlySpendingTarget(), 2, RoundingMode.HALF_UP);
        }

        List<TransactionResponse> recentTransactions = transactionRepository
                .findTop10ByMemberOrderByTransactionAtDesc(member).stream()
                .filter(t -> t.getCard() != null && t.getCard().getId().equals(cardId))
                .map(TransactionResponse::from)
                .collect(Collectors.toList());

        return CardDetailResponse.builder()
                .card(CardResponse.from(card))
                .benefits(benefits)
                .nextBilling(nextBilling)
                .monthlySpendingTarget(card.getMonthlySpendingTarget())
                .currentMonthSpending(card.getCurrentMonthSpending())
                .spendingAchievementRate(spendingRate)
                .totalBenefitThisMonth(totalBenefitThisMonth)
                .totalBenefitReceived(card.getTotalBenefitReceived())
                .recentTransactions(recentTransactions)
                .build();
    }

    public List<CardBillingResponse> getBillings(Long memberId, Long cardId) {
        Member member = memberService.findMemberById(memberId);
        Card card = findCardById(cardId);
        
        validateCardOwner(card, member);

        return cardBillingRepository.findByCardOrderByBillingYearDescBillingMonthDesc(card).stream()
                .map(CardBillingResponse::from)
                .collect(Collectors.toList());
    }

    public Card findCardById(Long cardId) {
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
