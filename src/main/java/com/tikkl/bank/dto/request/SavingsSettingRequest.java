package com.tikkl.bank.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingsSettingRequest {

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal savingsRatio;

    private Boolean autoSavingsEnabled;
}