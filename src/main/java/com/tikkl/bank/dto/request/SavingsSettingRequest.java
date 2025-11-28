package com.tikkl.bank.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class SavingsSettingRequest {

    @NotNull(message = "저축 비율을 입력해주세요")
    @DecimalMin(value = "0.0", message = "저축 비율은 0% 이상이어야 합니다")
    @DecimalMax(value = "100.0", message = "저축 비율은 100% 이하여야 합니다")
    private BigDecimal savingsRatio;

    private Boolean autoSavingsEnabled;
}
