package com.tikkl.bank.controller;

import com.tikkl.bank.common.ApiResponse;
import com.tikkl.bank.dto.request.TransactionSearchRequest;
import com.tikkl.bank.dto.response.TransactionResponse;
import com.tikkl.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/members/{memberId}/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactions(
            @PathVariable Long memberId,
            @ModelAttribute TransactionSearchRequest request) {
        Page<TransactionResponse> response = transactionService.searchTransactions(memberId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getRecentTransactions(
            @PathVariable Long memberId) {
        List<TransactionResponse> response = transactionService.getRecentTransactions(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransaction(
            @PathVariable Long memberId,
            @PathVariable Long transactionId) {
        TransactionResponse response = transactionService.getTransaction(memberId, transactionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
