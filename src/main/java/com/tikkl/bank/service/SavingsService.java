package com.tikkl.bank.service;

import com.tikkl.bank.dto.response.SavingsAccountResponse;
import com.tikkl.bank.entity.FinancialProduct;
import com.tikkl.bank.entity.Member;
import com.tikkl.bank.entity.SavingsAccount;
import com.tikkl.bank.entity.Transaction;
import com.tikkl.bank.exception.CustomException;
import com.tikkl.bank.exception.ErrorCode;
import com.tikkl.bank.repository.FinancialProductRepository;
import com.tikkl.bank.repository.SavingsAccountRepository;
import com.tikkl.bank.repository.TransactionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SavingsService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final MemberService memberService;
    private final TransactionRepository transactionRepository;
    private final FinancialProductRepository financialProductRepository;

    /**
     * 회원의 티끌통장 조회
     */
    public SavingsAccountResponse getSavingsAccount(Long memberId) {
        Member member = memberService.findMemberById(memberId);
        SavingsAccount savingsAccount = findSavingsAccountByMember(member);
        return SavingsAccountResponse.from(savingsAccount);
    }

    /**
     * 회원 기준 티끌통장 찾기 (없으면 예외)
     */
    public SavingsAccount findSavingsAccountByMember(Member member) {
        return savingsAccountRepository.findByMember(member)
            .orElseThrow(() -> new SavingsException(ErrorCode.SAVINGS_ACCOUNT_NOT_FOUND));
    }

    /**
     * 저축 관련 공통 예외
     */
    public static class SavingsException extends CustomException {

        public SavingsException(ErrorCode errorCode) {
            super(errorCode);
        }
    }

    @Transactional
    public void applyDailyInterest(LocalDate date) {

        List<SavingsAccount> accounts = savingsAccountRepository.findByIsActiveTrue();

        for (SavingsAccount account : accounts) {

            // 이미 해당 날짜에 이자 적용했으면 스킵
            if (date.equals(account.getLastInterestAppliedDate())) {
                continue;
            }

            BigDecimal balance = account.getBalance();
            BigDecimal rate = account.getInterestRate();

            if (balance == null || balance.compareTo(BigDecimal.ZERO) <= 0 || rate == null) {
                continue;
            }

            BigDecimal dailyRate = rate.divide(BigDecimal.valueOf(365), 8, RoundingMode.DOWN);
            BigDecimal interest = balance.multiply(dailyRate)
                .setScale(0, RoundingMode.DOWN);

            if (interest.compareTo(BigDecimal.ZERO) > 0) {
                account.setBalance(balance.add(interest));
                account.setTotalInterest(account.getTotalInterest().add(interest));
                account.setLastInterestAppliedDate(date);

                Transaction tx = Transaction.builder()
                    .member(account.getMember())
                    .transactionType(Transaction.TransactionType.INTEREST)
                    .amount(interest)
                    .balanceAfter(account.getBalance())
                    .description("저축 계좌 이자 적립 (" + date + ")")
                    .status(Transaction.TransactionStatus.COMPLETED)
                    .build();

                transactionRepository.save(tx);
            }
        }
    }

    /**
     * 금융상품 연결 - 선택한 상품으로 티끌통장 이자율/상품정보 설정 - 마지막 이자 적용일을 초기화해서 다시 계산 시작
     */
    @Transactional
    public SavingsAccountResponse linkFinancialProduct(Long memberId, Long productId) {
        Member member = memberService.findMemberById(memberId);

        SavingsAccount savingsAccount = savingsAccountRepository.findByMember(member)
            .orElseThrow(() -> new SavingsException(ErrorCode.SAVINGS_ACCOUNT_NOT_FOUND));

        FinancialProduct product = financialProductRepository.findById(productId)
            .orElseThrow(() -> new SavingsException(ErrorCode.PRODUCT_NOT_FOUND));

        // 금융상품 연결
        savingsAccount.setFinancialProduct(product);

        // 계좌 이자율을 금융상품 이자율로 맞추기
        savingsAccount.setInterestRate(product.getInterestRate());

        // 마지막 이자 적용일 초기화 (다음 applyDailyInterest 호출부터 다시 계산)
        savingsAccount.setLastInterestAppliedDate(null);

        return SavingsAccountResponse.from(savingsAccount);
    }

    public SavingsAccount getSavingsAccountEntity(Long memberId) {
        Member member = memberService.findMemberById(memberId);
        return savingsAccountRepository.findByMember(member)
            .orElseThrow(() -> new SavingsException(ErrorCode.SAVINGS_ACCOUNT_NOT_FOUND));
    }

    /**
     * 자동 저축 처리 - 결제 금액 100%를 티끌통장에 적립
     */
    @Transactional
    public void addAutoSavings(SavingsAccount savings, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        // 잔액 증가
        savings.setBalance(savings.getBalance().add(amount));

        // 누적 저축 증가
        savings.setTotalSaved(savings.getTotalSaved().add(amount));

        // 거래내역 생성 (SAVINGS)
        Transaction tx = Transaction.builder()
            .member(savings.getMember())
            .savingsAmount(amount)
            .amount(amount)
            .transactionType(Transaction.TransactionType.SAVINGS)
            .description("자동 저축 적립")
            .status(Transaction.TransactionStatus.COMPLETED)
            .balanceAfter(savings.getBalance())
            .build();

        transactionRepository.save(tx);
    }
}