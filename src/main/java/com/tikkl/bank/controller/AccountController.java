package com.tikkl.bank.controller;

import com.tikkl.bank.common.ApiResponse;
import com.tikkl.bank.dto.request.AccountRequest;
import com.tikkl.bank.dto.response.AccountResponse;
import com.tikkl.bank.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/members/{memberId}/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAccounts(@PathVariable Long memberId) {
        List<AccountResponse> response = accountService.getAccounts(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(
            @PathVariable Long memberId,
            @PathVariable Long accountId) {
        AccountResponse response = accountService.getAccount(memberId, accountId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> registerAccount(
            @PathVariable Long memberId,
            @Valid @RequestBody AccountRequest request) {
        AccountResponse response = accountService.registerAccount(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "계좌가 등록되었습니다"));
    }

    @PutMapping("/{accountId}/primary")
    public ResponseEntity<ApiResponse<AccountResponse>> setPrimaryAccount(
            @PathVariable Long memberId,
            @PathVariable Long accountId) {
        AccountResponse response = accountService.setPrimaryAccount(memberId, accountId);
        return ResponseEntity.ok(ApiResponse.success(response, "주 계좌가 설정되었습니다"));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @PathVariable Long memberId,
            @PathVariable Long accountId) {
        accountService.deleteAccount(memberId, accountId);
        return ResponseEntity.ok(ApiResponse.success(null, "계좌가 삭제되었습니다"));
    }
}
