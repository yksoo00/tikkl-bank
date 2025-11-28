package com.tikkl.bank.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {

    @NotBlank(message = "계좌번호를 입력해주세요")
    @Size(max = 30, message = "계좌번호는 30자 이하여야 합니다")
    private String accountNumber;

    @NotBlank(message = "은행명을 입력해주세요")
    @Size(max = 50, message = "은행명은 50자 이하여야 합니다")
    private String bankName;

    private Boolean isPrimary;
}
