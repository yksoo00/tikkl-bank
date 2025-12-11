package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.SavingsAccount;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SavingsAccountResponse {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private BigDecimal totalSaved;
    private BigDecimal totalInterest;
    private LocalDate maturityDate;
    private BigDecimal interestRate;
    private String productName;

    public static SavingsAccountResponse from(SavingsAccount s) {
        return SavingsAccountResponse.builder()
            .id(s.getId())
            .accountNumber(s.getAccountNumber())
            .balance(s.getBalance())
            .totalSaved(s.getTotalSaved())
            .totalInterest(s.getTotalInterest())
            .maturityDate(s.getMaturityDate())
            .interestRate(s.getInterestRate())
            .productName(
                s.getFinancialProduct() != null ? s.getFinancialProduct().getProductName() : null)
            .build();
    }
}