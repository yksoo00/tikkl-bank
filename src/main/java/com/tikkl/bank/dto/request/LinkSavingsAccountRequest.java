package com.tikkl.bank.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkSavingsAccountRequest {

    @NotNull
    private Long savingsAccountId;

    private BigDecimal monthlySpendingTarget;
}