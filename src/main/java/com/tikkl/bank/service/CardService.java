package com.tikkl.bank.service;

import com.tikkl.bank.dto.request.CardPaymentRequest;
import com.tikkl.bank.dto.response.CardBillingResponse;
import com.tikkl.bank.dto.response.CardDetailResponse;
import com.tikkl.bank.dto.response.CardPaymentResponse;
import com.tikkl.bank.dto.response.CardResponse;
import com.tikkl.bank.dto.response.TransactionResponse;
import com.tikkl.bank.entity.Account;
import com.tikkl.bank.entity.Card;
import com.tikkl.bank.entity.CardBilling;
import com.tikkl.bank.entity.CardProduct;
import com.tikkl.bank.entity.CardProductBenefit;
import com.tikkl.bank.entity.Member;
import com.tikkl.bank.entity.SavingsAccount;
import com.tikkl.bank.entity.Transaction;
import com.tikkl.bank.exception.CustomException;
import com.tikkl.bank.exception.ErrorCode;
import com.tikkl.bank.repository.AccountRepository;
import com.tikkl.bank.repository.CardBillingRepository;
import com.tikkl.bank.repository.CardProductBenefitRepository;
import com.tikkl.bank.repository.CardProductRepository;
import com.tikkl.bank.repository.CardRepository;
import com.tikkl.bank.repository.SavingsAccountRepository;
import com.tikkl.bank.repository.TransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final CardBillingRepository cardBillingRepository;

    private final MemberService memberService;
    private final AccountRepository accountRepository;

    private final SavingsService savingsService;

    private final CardProductRepository cardProductRepository;
    private final CardProductBenefitRepository cardProductBenefitRepository;
    private final SavingsAccountRepository savingsAccountRepository;

    // =============================================================
    // [1] 카드 발급 (마스터 카드 기반)
    // =============================================================
    @Transactional
    public Long registerCardFromProduct(Long memberId, Long cardProductId,
        Long paymentAccountId, String nickname) {

        Member member = memberService.findMemberById(memberId);
        CardProduct product = cardProductRepository.findById(cardProductId)
            .orElseThrow(() -> new CustomException(ErrorCode.CARD_PRODUCT_NOT_FOUND));

        Account paymentAccount = accountRepository.findById(paymentAccountId)
            .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 카드 생성
        Card card = new Card();
        card.setMember(member);
        card.setCardProduct(product);
        card.setPaymentAccount(paymentAccount);
        card.setNickname(nickname);
        card.setIsActive(true);
        card.setCurrentMonthSpending(BigDecimal.ZERO);
        card.setTotalBenefitReceived(BigDecimal.ZERO);

        // 카드번호는 서버에서 생성 (간단하게 4자리만)
        String last4 = String.valueOf((int) (Math.random() * 9000) + 1000);
        card.setLastFourDigits(last4);
        card.setMaskedCardNumber("**** **** **** " + last4);

        cardRepository.save(card);
        return card.getId();
    }

    // =============================================================
    // [2] 카드 목록 조회
    // =============================================================
    public List<CardResponse> getCards(Long memberId) {
        Member member = memberService.findMemberById(memberId);

        return cardRepository.findByMemberAndIsActiveTrue(member)
            .stream()
            .map(CardResponse::from)
            .collect(Collectors.toList());
    }

    // =============================================================
    // [3] 카드 단건 조회
    // =============================================================
    public CardResponse getCard(Long memberId, Long cardId) {
        Member member = memberService.findMemberById(memberId);
        Card card = findCardById(cardId);
        validateOwner(card, member);

        return CardResponse.from(card);
    }

    // =============================================================
    // [4] 결제 계좌 설정
    // =============================================================
    @Transactional
    public CardResponse setPaymentAccount(Long memberId, Long cardId, Long accountId) {

        Member member = memberService.findMemberById(memberId);
        Card card = findCardById(cardId);
        validateOwner(card, member);

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (!account.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.ACCOUNT_OWNER_MISMATCH);
        }

        card.setPaymentAccount(account);
        return CardResponse.from(card);
    }

    // =============================================================
    // [5] 카드 해지
    // =============================================================
    @Transactional
    public void deactivateCard(Long memberId, Long cardId) {
        Member member = memberService.findMemberById(memberId);
        Card card = findCardById(cardId);
        validateOwner(card, member);
        card.setIsActive(false);
    }

    // =============================================================
    // [6] 카드 결제
    // =============================================================
    @Transactional
    public CardPaymentResponse processPayment(Long memberId, Long cardId,
        CardPaymentRequest request) {

        Member member = memberService.findMemberById(memberId);
        Card card = findCardById(cardId);
        validateOwner(card, member);

        BigDecimal amount = request.getAmount();
        String category = request.getCategory();

        // ======================================================
        // 1) 혜택 계산 (마스터 카드 기반)
        // ======================================================
        CardProduct product = card.getCardProduct();

        List<CardProductBenefit> benefits =
            cardProductBenefitRepository.findByCardProduct(product);

        BigDecimal currentSpending = card.getCurrentMonthSpending();
        BigDecimal afterSpending = currentSpending.add(amount);

        CardProductBenefit applied = null;
        BigDecimal appliedTarget = BigDecimal.ZERO;

        for (CardProductBenefit b : benefits) {

            if (!matchesCategory(category, b.getCategoryCode())) {
                continue;
            }

            BigDecimal target = b.getMinSpendingForActivation() == null
                ? BigDecimal.ZERO
                : b.getMinSpendingForActivation();

            if (afterSpending.compareTo(target) >= 0) {
                if (applied == null || target.compareTo(appliedTarget) > 0) {
                    applied = b;
                    appliedTarget = target;
                }
            }
        }

        BigDecimal discount = BigDecimal.ZERO;

        if (applied != null && applied.getDiscountRate() != null) {

            discount = amount.multiply(applied.getDiscountRate())
                .setScale(0, RoundingMode.DOWN);

            if (applied.getMaxDiscountPerMonth() != null) {
                discount = discount.min(applied.getMaxDiscountPerMonth());
            }
        }

        // =========================================================================================
        // 2) Billing 업데이트 (다음 달 청구)
        // =========================================================================================
        YearMonth nextMonth = YearMonth.now().plusMonths(1);

        CardBilling billing = cardBillingRepository
            .findByCardAndBillingYearAndBillingMonth(
                card, nextMonth.getYear(), nextMonth.getMonthValue())
            .orElse(CardBilling.builder()
                .card(card)
                .billingYear(nextMonth.getYear())
                .billingMonth(nextMonth.getMonthValue())
                .totalAmount(BigDecimal.ZERO)
                .benefitAmount(BigDecimal.ZERO)
                .savedAmount(BigDecimal.ZERO)
                .transactionCount(0)
                .status(CardBilling.BillingStatus.PENDING)
                .build());

        BigDecimal netAmount = amount.subtract(discount); // 실제 결제 금액
        if (netAmount.compareTo(BigDecimal.ZERO) < 0) {
            netAmount = BigDecimal.ZERO;
        }

        billing.setTotalAmount(billing.getTotalAmount().add(netAmount));
        billing.setBenefitAmount(billing.getBenefitAmount().add(discount));
        billing.setTransactionCount(billing.getTransactionCount() + 1);

        cardBillingRepository.save(billing);

        // ======================================================
        // 3) 카드 월 실적 업데이트
        // ======================================================
        card.setCurrentMonthSpending(card.getCurrentMonthSpending().add(amount));
        card.setTotalBenefitReceived(card.getTotalBenefitReceived().add(discount));

        cardRepository.save(card);

        // ======================================================
        // 4) 거래내역(Transaction) 생성
        // ======================================================
        Transaction tx = Transaction.builder()
            .member(member)
            .card(card)
            .transactionType(Transaction.TransactionType.PAYMENT)
            .amount(amount)
            .category(category)
            .merchant(request.getMerchant())
            .description(request.getDescription() != null
                ? request.getDescription()
                : "카드 결제")
            .status(Transaction.TransactionStatus.COMPLETED)
            .build();

        transactionRepository.save(tx);

        // ======================================================================================
        // 5) 실제 출금 계좌에서 돈 차감
        // ======================================================================================
        Account paymentAccount = card.getPaymentAccount();
        if (paymentAccount == null) {
            throw new RuntimeException("카드에 결제 계좌가 연결되어 있지 않습니다.");
        }

        BigDecimal newBalance = paymentAccount.getBalance().subtract(netAmount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("출금 계좌 잔액이 부족합니다.");
        }

        paymentAccount.setBalance(newBalance);
        accountRepository.save(paymentAccount);

        // ======================================================================================
        // 6) 자동저축 (티끌뱅크 계좌로 100% 전송)
        // ======================================================================================
        SavingsAccount savings = savingsService.getSavingsAccountEntity(memberId);

        BigDecimal savingsAmount = netAmount; // 자동저축 = 결제 금액 100%
        savingsService.addAutoSavings(savings, savingsAmount);

        // 트랜잭션에 자동저축 정보 업데이트
        tx.setSavingsAmount(savingsAmount);
        transactionRepository.save(tx);

        // ======================================================================================
        // 7) 응답 반환
        // ======================================================================================
        return CardPaymentResponse.builder()
            .transactionId(tx.getId())
            .paymentAmount(amount)
            .benefitAmount(discount)
            .savingsAmount(savingsAmount)
            .merchant(request.getMerchant())
            .category(category)
            .cardMonthlyTotal(card.getCurrentMonthSpending())
            .nextBillingAmount(billing.getTotalAmount())
            .build();
    }

    // =============================
    //  헬퍼 메서드들
    // =============================


    // =============================================================
    // [7] 카드 상세 (혜택 제외, 실적 + billing + recent tx)
    // =============================================================
    public CardDetailResponse getCardDetail(Long memberId, Long cardId) {

        Member member = memberService.findMemberById(memberId);
        Card card = findCardById(cardId);
        validateOwner(card, member);

        // 다음달 Billing
        YearMonth nextMonth = YearMonth.now().plusMonths(1);
        CardBillingResponse billing = cardBillingRepository
            .findByCardAndBillingYearAndBillingMonth(
                card, nextMonth.getYear(), nextMonth.getMonthValue())
            .map(CardBillingResponse::from)
            .orElse(null);

        // 최근 거래내역
        List<TransactionResponse> recent =
            transactionRepository.findTop10ByMemberOrderByTransactionAtDesc(member)
                .stream()
                .filter(t -> t.getCard() != null && t.getCard().getId().equals(cardId))
                .map(TransactionResponse::from)
                .collect(Collectors.toList());

        return CardDetailResponse.builder()
            .card(CardResponse.from(card))
            .nextBilling(billing)
            .currentMonthSpending(card.getCurrentMonthSpending())
            .totalBenefitReceived(card.getTotalBenefitReceived())
            .recentTransactions(recent)
            .build();
    }

    // =============================================================
    // [8] 매달 1일 자동 청구 처리
    // =============================================================
    @Scheduled(cron = "0 0 4 1 * *")
    @Transactional
    public void payLastMonthBills() {

        YearMonth lastMonth = YearMonth.now().minusMonths(1);

        List<CardBilling> billings =
            cardBillingRepository.findByBillingYearAndBillingMonthAndStatus(
                lastMonth.getYear(),
                lastMonth.getMonthValue(),
                CardBilling.BillingStatus.PENDING
            );

        for (CardBilling billing : billings) {

            Card card = billing.getCard();
            Account payAcc = card.getPaymentAccount();

            if (payAcc == null) {
                continue;
            }

            BigDecimal amount = billing.getTotalAmount();

            if (payAcc.getBalance().compareTo(amount) < 0) {
                billing.setStatus(CardBilling.BillingStatus.FAILED);
                continue;
            }

            // 출금
            payAcc.setBalance(payAcc.getBalance().subtract(amount));
            accountRepository.save(payAcc);

            // 거래내역 기록
            Transaction tx = Transaction.builder()
                .member(payAcc.getMember())
                .card(card)
                .transactionType(Transaction.TransactionType.PAYMENT)
                .amount(amount.negate())
                .balanceAfter(payAcc.getBalance())
                .description("신용카드 자동결제")
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();
            transactionRepository.save(tx);

            billing.setStatus(CardBilling.BillingStatus.PAID);
        }
    }

    // =============================================================
    // 헬퍼 메서드
    // =============================================================
    private Card findCardById(Long id) {
        return cardRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.CARD_NOT_FOUND));
    }

    private void validateOwner(Card card, Member member) {
        if (!card.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.CARD_OWNER_MISMATCH);
        }
    }

    private boolean matchesCategory(String requestCat, String benefitCat) {
        if (benefitCat == null || benefitCat.isBlank()) {
            return false;
        }
        if (requestCat == null || requestCat.isBlank()) {
            return false;
        }
        return requestCat.equalsIgnoreCase(benefitCat);
    }

    // ==========================================
// [8] 매달 1일 티끌통장에서 지난달 카드 청구 자동결제
// ==========================================
    @Scheduled(cron = "0 0 4 1 * *")   // 매달 1일 새벽 4시
    @Transactional
    public void payLastMonthBillsFromTikkl() {

        YearMonth lastMonth = YearMonth.now().minusMonths(1);

        var billings = cardBillingRepository.findByBillingYearAndBillingMonthAndStatus(
            lastMonth.getYear(),
            lastMonth.getMonthValue(),
            CardBilling.BillingStatus.PENDING
        );

        for (CardBilling billing : billings) {

            Card card = billing.getCard();
            Member member = card.getMember();

            // 1) 회원의 티끌통장 조회
            var tikkl = savingsAccountRepository.findByMember(member)
                .orElseThrow(() ->
                    new CustomException(ErrorCode.SAVINGS_ACCOUNT_NOT_FOUND)
                );

            var amount = billing.getTotalAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                // 청구 금액이 0원 이하 → 그냥 PAID 처리
                billing.setStatus(CardBilling.BillingStatus.PAID);
                continue;
            }

            // 2) 티끌 잔액 부족 → FAILED
            if (tikkl.getBalance().compareTo(amount) < 0) {
                billing.setStatus(CardBilling.BillingStatus.FAILED);
                continue;
            }

            // 3) 티끌통장에서 출금
            tikkl.setBalance(tikkl.getBalance().subtract(amount));
            savingsAccountRepository.save(tikkl);

            // 4) 거래내역 기록 (티끌통장 기준)
            Transaction tx = Transaction.builder()
                .member(member)
                .card(card)
                .transactionType(Transaction.TransactionType.PAYMENT)
                .amount(amount.negate())                 // 출금 → 음수
                .balanceAfter(tikkl.getBalance())
                .description("지난달 카드 청구 자동결제(티끌통장)")
                .status(Transaction.TransactionStatus.COMPLETED)
                .build();

            transactionRepository.save(tx);

            // 5) 청구 상태 변경
            billing.setStatus(CardBilling.BillingStatus.PAID);
        }
    }
}