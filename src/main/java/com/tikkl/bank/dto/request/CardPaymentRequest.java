package com.tikkl.bank.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardPaymentRequest {

    @NotNull(message = "결제 금액을 입력해주세요")
    @DecimalMin("1")
    private BigDecimal amount;

    private String merchant;

    /**
     * 카테고리 : RESTAURANT, CAFE, MOVIE 등 ENUM화 예정
     */
    private String category;

    private String description;
}