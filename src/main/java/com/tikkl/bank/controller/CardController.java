package com.tikkl.bank.controller;

import com.tikkl.bank.common.ApiResponse;
import com.tikkl.bank.dto.request.CardPaymentRequest;
import com.tikkl.bank.dto.request.SetPaymentAccountRequest;
import com.tikkl.bank.dto.response.CardDetailResponse;
import com.tikkl.bank.dto.response.CardPaymentResponse;
import com.tikkl.bank.dto.response.CardResponse;
import com.tikkl.bank.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/{memberId}/cards")
public class CardController {

    private final CardService cardService;

    /** 1. 마스터 카드 기반 카드 발급 */
    @PostMapping
    public ApiResponse<Long> registerCardFromProduct(
        @PathVariable Long memberId,
        @RequestParam Long productId,
        @RequestParam Long accountId,
        @RequestParam(required = false) String nickname
    ) {
        return ApiResponse.success(
            cardService.registerCardFromProduct(memberId, productId, accountId, nickname)
        );
    }

    /** 2. 카드 전체 조회 */
    @GetMapping
    public ApiResponse<List<CardResponse>> getCards(@PathVariable Long memberId) {
        return ApiResponse.success(cardService.getCards(memberId));
    }

    /** 3. 카드 상세 조회 */
    @GetMapping("/{cardId}")
    public ApiResponse<CardDetailResponse> getCardDetail(
        @PathVariable Long memberId,
        @PathVariable Long cardId
    ) {
        return ApiResponse.success(cardService.getCardDetail(memberId, cardId));
    }

    /** 4. 카드 결제 */
    @PostMapping("/{cardId}/payments")
    public ApiResponse<CardPaymentResponse> processPayment(
        @PathVariable Long memberId,
        @PathVariable Long cardId,
        @RequestBody CardPaymentRequest request
    ) {
        return ApiResponse.success(cardService.processPayment(memberId, cardId, request));
    }

    /** 5. 카드 결제 계좌 설정 */
    @PutMapping("/{cardId}/payment-account")
    public ApiResponse<CardResponse> setPaymentAccount(
        @PathVariable Long memberId,
        @PathVariable Long cardId,
        @RequestBody SetPaymentAccountRequest request
    ) {
        return ApiResponse.success(
            cardService.setPaymentAccount(memberId, cardId, request.getAccountId())
        );
    }

    /** 6. 카드 비활성화 */
    @DeleteMapping("/{cardId}")
    public ApiResponse<Void> deactivateCard(
        @PathVariable Long memberId,
        @PathVariable Long cardId
    ) {
        cardService.deactivateCard(memberId, cardId);
        return ApiResponse.success(null, "카드가 비활성화되었습니다.");
    }
}