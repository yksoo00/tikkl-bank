package com.tikkl.bank.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CardPaymentRequest {

    @NotNull(message = "결제 금액을 입력해주세요")
    @DecimalMin(value = "1", message = "결제 금액은 1원 이상이어야 합니다")
    private BigDecimal amount;

    private String merchant; // 가맹점

    private String category; // 카테고리 (식비, 교통, 쇼핑 등)

    private String description;
}
