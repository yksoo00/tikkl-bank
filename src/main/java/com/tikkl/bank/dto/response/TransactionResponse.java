package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.Transaction;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionResponse {

    private Long id;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal savingsAmount;
    private BigDecimal balanceAfter;
    private String description;
    private String merchant;
    private String category;
    private String status;
    private LocalDateTime transactionAt;

    public static TransactionResponse from(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .transactionType(transaction.getTransactionType().name())
                .amount(transaction.getAmount())
                .savingsAmount(transaction.getSavingsAmount())
                .balanceAfter(transaction.getBalanceAfter())
                .description(transaction.getDescription())
                .merchant(transaction.getMerchant())
                .category(transaction.getCategory())
                .status(transaction.getStatus().name())
                .transactionAt(transaction.getTransactionAt())
                .build();
    }
}
