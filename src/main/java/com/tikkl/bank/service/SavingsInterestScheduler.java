// SavingsInterestScheduler.java (새 파일)

package com.tikkl.bank.service;

import com.tikkl.bank.entity.SavingsAccount;
import com.tikkl.bank.entity.Transaction;
import com.tikkl.bank.repository.SavingsAccountRepository;
import com.tikkl.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsInterestScheduler {

    private final SavingsAccountRepository savingsAccountRepository;
    private final TransactionRepository transactionRepository;

    /**
     * 매일 새벽 3시에 저축 계좌 이자 정산
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void applyDailyInterest() {
        LocalDate today = LocalDate.now();

        List<SavingsAccount> accounts = savingsAccountRepository.findByIsActiveTrue();

        for (SavingsAccount account : accounts) {

            // 이미 오늘 이자를 적용했다면 스킵
            if (today.equals(account.getLastInterestAppliedDate())) {
                continue;
            }

            BigDecimal balance = account.getBalance();
            BigDecimal rate = account.getInterestRate(); // 연 이율 (예: 0.03)

            if (balance == null || balance.compareTo(BigDecimal.ZERO) <= 0 || rate == null) {
                continue;
            }

            // 연 이율 → 일 이율
            BigDecimal dailyRate = rate.divide(BigDecimal.valueOf(365), 8, RoundingMode.DOWN);

            BigDecimal interest = balance.multiply(dailyRate)
                .setScale(0, RoundingMode.DOWN); // 원 단위 절사 예시

            if (interest.compareTo(BigDecimal.ZERO) > 0) {
                // 잔액 + 누적 이자 갱신
                account.setBalance(balance.add(interest));
                account.setTotalInterest(account.getTotalInterest().add(interest));
                account.setLastInterestAppliedDate(today);

                // 거래 내역 추가 (INTEREST)
                Transaction interestTx = Transaction.builder()
                    .member(account.getMember())
                    .transactionType(Transaction.TransactionType.INTEREST)
                    .amount(interest)
                    .balanceAfter(account.getBalance())
                    .description("저축 계좌 이자 적립")
                    .status(Transaction.TransactionStatus.COMPLETED)
                    .build();

                transactionRepository.save(interestTx);
            }
        }
    }
}