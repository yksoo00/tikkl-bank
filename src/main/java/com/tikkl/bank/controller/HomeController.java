package com.tikkl.bank.controller;

import com.tikkl.bank.common.ApiResponse;
import com.tikkl.bank.dto.response.DashboardResponse;
import com.tikkl.bank.dto.response.HomeResponse;
import com.tikkl.bank.dto.response.SavingsAccountResponse;
import com.tikkl.bank.service.DashboardService;
import com.tikkl.bank.service.HomeService;
import com.tikkl.bank.service.SavingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members/{memberId}")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;
    private final SavingsService savingsService;
    private final DashboardService dashboardService;

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<HomeResponse>> getHome(@PathVariable Long memberId) {
        HomeResponse response = homeService.getHomeData(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/savings")
    public ResponseEntity<ApiResponse<SavingsAccountResponse>> getSavingsAccount(@PathVariable Long memberId) {
        SavingsAccountResponse response = savingsService.getSavingsAccount(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(@PathVariable Long memberId) {
        DashboardResponse response = dashboardService.getDashboard(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
