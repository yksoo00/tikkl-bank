package com.tikkl.bank.dto.response;

import com.tikkl.bank.entity.Account;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private String bankName;
    private String accountType;
    private BigDecimal balance;
    private Boolean isPrimary;
    private Boolean isActive;

    public static AccountResponse from(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .bankName(account.getBankName())
                .accountType(account.getAccountType().name())
                .balance(account.getBalance())
                .isPrimary(account.getIsPrimary())
                .isActive(account.getIsActive())
                .build();
    }
}
