package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.SavingsAccount;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class SavingsAccountResponse {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal totalSaved;
    private BigDecimal totalInterest;
    private BigDecimal interestRate;
    private LocalDate maturityDate;
    private Long remainingDays;
    private BigDecimal expectedInterest;

    public static SavingsAccountResponse from(SavingsAccount savingsAccount) {
        long remainingDays = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(), savingsAccount.getMaturityDate());
        
        // 예상 이자 계산 (단리 기준)
        BigDecimal expectedInterest = savingsAccount.getBalance()
                .multiply(savingsAccount.getInterestRate())
                .multiply(BigDecimal.valueOf(remainingDays))
                .divide(BigDecimal.valueOf(365), 2, java.math.RoundingMode.HALF_UP);

        return SavingsAccountResponse.builder()
                .id(savingsAccount.getId())
                .accountNumber(savingsAccount.getAccountNumber())
                .balance(savingsAccount.getBalance())
                .totalSaved(savingsAccount.getTotalSaved())
                .totalInterest(savingsAccount.getTotalInterest())
                .interestRate(savingsAccount.getInterestRate())
                .maturityDate(savingsAccount.getMaturityDate())
                .remainingDays(Math.max(0, remainingDays))
                .expectedInterest(expectedInterest.compareTo(BigDecimal.ZERO) > 0 ? expectedInterest : BigDecimal.ZERO)
                .build();
    }
}
