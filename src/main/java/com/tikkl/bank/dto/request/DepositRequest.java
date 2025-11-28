package com.tikkl.bank.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class DepositRequest {

    @NotNull(message = "입금 금액을 입력해주세요")
    @DecimalMin(value = "1", message = "입금 금액은 1원 이상이어야 합니다")
    private BigDecimal amount;

    private String description;
}
