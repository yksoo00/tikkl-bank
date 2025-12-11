package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.Transaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionResponse {

    private Long id;
    private String description;
    private String merchant;
    private String category;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String transactionType;
    private LocalDateTime transactionAt;

    public static TransactionResponse from(Transaction tx) {
        return TransactionResponse.builder()
            .id(tx.getId())
            .description(tx.getDescription())
            .merchant(tx.getMerchant())
            .category(tx.getCategory())
            .amount(tx.getAmount())
            .balanceAfter(tx.getBalanceAfter())
            .transactionType(tx.getTransactionType().name())
            .transactionAt(tx.getTransactionAt())
            .build();
    }
}