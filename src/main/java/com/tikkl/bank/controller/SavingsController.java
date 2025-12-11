package com.tikkl.bank.controller;

import com.tikkl.bank.common.ApiResponse;
import com.tikkl.bank.dto.request.LinkProductRequest;
import com.tikkl.bank.dto.response.SavingsAccountResponse;
import com.tikkl.bank.service.SavingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/{memberId}/savings")
public class SavingsController {

    private final SavingsService savingsService;

    @GetMapping
    public ApiResponse<SavingsAccountResponse> getSavings(@PathVariable Long memberId) {
        return ApiResponse.success(savingsService.getSavingsAccount(memberId));
    }

    @PutMapping("/product")
    public ApiResponse<SavingsAccountResponse> linkProduct(
        @PathVariable Long memberId,
        @RequestBody LinkProductRequest request
    ) {
        return ApiResponse.success(
            savingsService.linkFinancialProduct(memberId, request.getProductId())
        );
    }
}