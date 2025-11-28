package com.tikkl.bank.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CardBenefitRequest {

    @NotBlank(message = "혜택 이름을 입력해주세요")
    private String benefitName;

    private String benefitDescription;

    @NotBlank(message = "혜택 유형을 선택해주세요")
    private String benefitType; // DISCOUNT, CASHBACK, POINT, MILEAGE

    @NotNull(message = "할인율을 입력해주세요")
    @DecimalMin(value = "0", message = "할인율은 0% 이상이어야 합니다")
    @DecimalMax(value = "100", message = "할인율은 100% 이하여야 합니다")
    private BigDecimal discountRate;

    private BigDecimal maxDiscount;

    @DecimalMin(value = "0", message = "목표 금액은 0원 이상이어야 합니다")
    private BigDecimal targetAmount;

    private String category;
}
