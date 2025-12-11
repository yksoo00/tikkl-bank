package com.tikkl.bank.controller;

import com.tikkl.bank.common.ApiResponse;
import com.tikkl.bank.dto.request.AccountRequest;
import com.tikkl.bank.dto.request.DepositRequest;
import com.tikkl.bank.dto.request.WithdrawRequest;
import com.tikkl.bank.dto.response.AccountResponse;
import com.tikkl.bank.dto.response.TransactionResponse;
import com.tikkl.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/{memberId}/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ApiResponse<List<AccountResponse>> getAccounts(@PathVariable Long memberId) {
        return ApiResponse.success(accountService.getAccounts(memberId));
    }

    @GetMapping("/{accountId}")
    public ApiResponse<AccountResponse> getAccount(
        @PathVariable Long memberId,
        @PathVariable Long accountId
    ) {
        return ApiResponse.success(accountService.getAccount(memberId, accountId));
    }

    @PostMapping
    public ApiResponse<AccountResponse> registerAccount(
        @PathVariable Long memberId,
        @RequestBody AccountRequest request
    ) {
        return ApiResponse.success(accountService.registerAccount(memberId, request));
    }

    @PutMapping("/{accountId}/primary")
    public ApiResponse<AccountResponse> setPrimaryAccount(
        @PathVariable Long memberId,
        @PathVariable Long accountId
    ) {
        return ApiResponse.success(accountService.setPrimaryAccount(memberId, accountId));
    }

    @DeleteMapping("/{accountId}")
    public ApiResponse<Void> deleteAccount(
        @PathVariable Long memberId,
        @PathVariable Long accountId
    ) {
        accountService.deleteAccount(memberId, accountId);
        return ApiResponse.success(null, "계좌가 삭제되었습니다.");
    }

    @PostMapping("/{accountId}/deposit")
    public ApiResponse<TransactionResponse> deposit(
        @PathVariable Long memberId,
        @PathVariable Long accountId,
        @RequestBody DepositRequest request
    ) {
        return ApiResponse.success(accountService.deposit(memberId, accountId, request));
    }

    @PostMapping("/{accountId}/withdraw")
    public ApiResponse<TransactionResponse> withdraw(
        @PathVariable Long memberId,
        @PathVariable Long accountId,
        @RequestBody WithdrawRequest request
    ) {
        return ApiResponse.success(accountService.withdraw(memberId, accountId, request));
    }
}