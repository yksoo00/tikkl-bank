package com.tikkl.bank.controller;

import com.tikkl.bank.common.ApiResponse;
import com.tikkl.bank.dto.request.TransactionSearchRequest;
import com.tikkl.bank.dto.response.TransactionResponse;
import com.tikkl.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/{memberId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/recent")
    public ApiResponse<List<TransactionResponse>> getRecent(@PathVariable Long memberId) {
        return ApiResponse.success(transactionService.getRecentTransactions(memberId));
    }

    @PostMapping("/search")
    public ApiResponse<Page<TransactionResponse>> search(
        @PathVariable Long memberId,
        @RequestBody TransactionSearchRequest request
    ) {
        return ApiResponse.success(transactionService.searchTransactions(memberId, request));
    }

    @GetMapping("/{transactionId}")
    public ApiResponse<TransactionResponse> getDetail(
        @PathVariable Long memberId,
        @PathVariable Long transactionId
    ) {
        return ApiResponse.success(transactionService.getTransaction(memberId, transactionId));
    }
}