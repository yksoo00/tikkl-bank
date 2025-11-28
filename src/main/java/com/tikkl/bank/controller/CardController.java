package com.tikkl.bank.controller;

import com.tikkl.bank.common.ApiResponse;
import com.tikkl.bank.dto.request.*;
import com.tikkl.bank.dto.response.*;
import com.tikkl.bank.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/members/{memberId}/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CardResponse>>> getCards(@PathVariable Long memberId) {
        List<CardResponse> response = cardService.getCards(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<ApiResponse<CardResponse>> getCard(
            @PathVariable Long memberId,
            @PathVariable Long cardId) {
        CardResponse response = cardService.getCard(memberId, cardId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CardResponse>> registerCard(
            @PathVariable Long memberId,
            @Valid @RequestBody CardRequest request) {
        CardResponse response = cardService.registerCard(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "카드가 등록되었습니다"));
    }

    @PutMapping("/{cardId}/bonus-ratio")
    public ResponseEntity<ApiResponse<CardResponse>> updateBonusSavingsRatio(
            @PathVariable Long memberId,
            @PathVariable Long cardId,
            @RequestParam BigDecimal ratio) {
        CardResponse response = cardService.updateBonusSavingsRatio(memberId, cardId, ratio);
        return ResponseEntity.ok(ApiResponse.success(response, "보너스 저축 비율이 설정되었습니다"));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<ApiResponse<Void>> deactivateCard(
            @PathVariable Long memberId,
            @PathVariable Long cardId) {
        cardService.deactivateCard(memberId, cardId);
        return ResponseEntity.ok(ApiResponse.success(null, "카드가 비활성화되었습니다"));
    }

    @GetMapping("/{cardId}/detail")
    public ResponseEntity<ApiResponse<CardDetailResponse>> getCardDetail(
            @PathVariable Long memberId,
            @PathVariable Long cardId) {
        CardDetailResponse response = cardService.getCardDetail(memberId, cardId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{cardId}/payment")
    public ResponseEntity<ApiResponse<CardPaymentResponse>> processPayment(
            @PathVariable Long memberId,
            @PathVariable Long cardId,
            @Valid @RequestBody CardPaymentRequest request) {
        CardPaymentResponse response = cardService.processPayment(memberId, cardId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "결제가 완료되었습니다"));
    }

    @PostMapping("/{cardId}/link-savings")
    public ResponseEntity<ApiResponse<CardResponse>> linkSavingsAccount(
            @PathVariable Long memberId,
            @PathVariable Long cardId,
            @Valid @RequestBody LinkSavingsAccountRequest request) {
        CardResponse response = cardService.linkSavingsAccount(memberId, cardId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "저축 계좌가 연결되었습니다"));
    }

    @GetMapping("/{cardId}/benefits")
    public ResponseEntity<ApiResponse<List<CardBenefitResponse>>> getBenefits(
            @PathVariable Long memberId,
            @PathVariable Long cardId) {
        List<CardBenefitResponse> response = cardService.getBenefits(memberId, cardId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{cardId}/benefits")
    public ResponseEntity<ApiResponse<CardBenefitResponse>> addBenefit(
            @PathVariable Long memberId,
            @PathVariable Long cardId,
            @Valid @RequestBody CardBenefitRequest request) {
        CardBenefitResponse response = cardService.addBenefit(memberId, cardId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "혜택이 추가되었습니다"));
    }

    @GetMapping("/{cardId}/billings")
    public ResponseEntity<ApiResponse<List<CardBillingResponse>>> getBillings(
            @PathVariable Long memberId,
            @PathVariable Long cardId) {
        List<CardBillingResponse> response = cardService.getBillings(memberId, cardId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
