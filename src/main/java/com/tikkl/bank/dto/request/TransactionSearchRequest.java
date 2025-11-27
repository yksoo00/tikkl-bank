package com.tikkl.bank.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionSearchRequest {

    private String transactionType; // DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT, SAVINGS, INTEREST
    private String category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String keyword; // 검색어 (description, merchant)
    private Integer page = 0;
    private Integer size = 20;
}
