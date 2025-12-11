package com.tikkl.bank.dto.request;

import java.time.LocalDate;
import lombok.Data;

@Data
public class CardRequest {

    /** 마스터 카드 ID */
    private Long cardProductId;

    /** 카드 번호 (마스킹 저장) */
    private String cardNumber;

    /** 별칭 */
    private String nickname;

    /** 청구일 */
    private Integer billingDay;

    /** 유효기간(선택) */
    private LocalDate expiryDate;
}