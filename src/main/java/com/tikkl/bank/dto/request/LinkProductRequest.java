package com.tikkl.bank.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkProductRequest {

    @NotNull(message = "연결할 금융상품 ID를 입력해주세요.")
    private Long productId;
}