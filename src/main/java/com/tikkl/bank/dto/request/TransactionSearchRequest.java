package com.tikkl.bank.dto.request;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionSearchRequest {

    private String transactionType;
    private String category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String keyword;
    private Integer page = 0;
    private Integer size = 20;
}