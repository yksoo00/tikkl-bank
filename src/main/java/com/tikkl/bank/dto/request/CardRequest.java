package com.tikkl.bank.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CardRequest {

    @NotBlank(message = "카드번호를 입력해주세요")
    private String cardNumber;

    @NotBlank(message = "카드명을 입력해주세요")
    @Size(max = 50, message = "카드명은 50자 이하여야 합니다")
    private String cardName;

    @NotBlank(message = "카드사를 입력해주세요")
    @Size(max = 50, message = "카드사명은 50자 이하여야 합니다")
    private String cardCompany;

    @NotBlank(message = "카드 유형을 선택해주세요")
    private String cardType; // CREDIT, DEBIT, PREPAID

    @NotNull(message = "유효기간을 입력해주세요")
    @Future(message = "유효기간은 미래 날짜여야 합니다")
    private LocalDate expiryDate;

    @DecimalMin(value = "0.0", message = "보너스 저축 비율은 0% 이상이어야 합니다")
    @DecimalMax(value = "100.0", message = "보너스 저축 비율은 100% 이하여야 합니다")
    private BigDecimal bonusSavingsRatio;

    @DecimalMin(value = "0", message = "월 사용 목표 금액은 0원 이상이어야 합니다")
    private BigDecimal monthlySpendingTarget;

    private String benefits; // 카드 혜택 정보
}
