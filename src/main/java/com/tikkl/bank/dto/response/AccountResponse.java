package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.Account;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private String bankName;
    private boolean isPrimary;
    private BigDecimal balance;

    public static AccountResponse from(Account account) {
        return AccountResponse.builder()
            .id(account.getId())
            .accountNumber(account.getAccountNumber())
            .bankName(account.getBankName())
            .isPrimary(account.getIsPrimary())
            .balance(account.getBalance())
            .build();
    }
}
