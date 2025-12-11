package com.tikkl.bank.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetPaymentAccountRequest {

    @NotNull(message = "결제 계좌 ID를 입력해주세요")
    private Long accountId;
}