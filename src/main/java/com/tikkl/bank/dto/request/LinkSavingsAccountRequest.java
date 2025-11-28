package com.tikkl.bank.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class LinkSavingsAccountRequest {

    @NotNull(message = "저축 계좌 ID를 입력해주세요")
    private Long savingsAccountId;

    @DecimalMin(value = "0", message = "월 사용 목표 금액은 0원 이상이어야 합니다")
    private BigDecimal monthlySpendingTarget;
}
