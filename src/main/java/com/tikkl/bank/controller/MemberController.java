package com.tikkl.bank.controller;

import com.tikkl.bank.common.ApiResponse;
import com.tikkl.bank.dto.request.LoginRequest;
import com.tikkl.bank.dto.request.SavingsSettingRequest;
import com.tikkl.bank.dto.request.SignupRequest;
import com.tikkl.bank.dto.response.MemberResponse;
import com.tikkl.bank.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ApiResponse<MemberResponse> signup(@RequestBody SignupRequest request) {
        return ApiResponse.success(memberService.signup(request));
    }

    @PostMapping("/login")
    public ApiResponse<MemberResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(memberService.login(request));
    }

    @GetMapping("/{memberId}")
    public ApiResponse<MemberResponse> getMember(@PathVariable Long memberId) {
        return ApiResponse.success(memberService.getMember(memberId));
    }

    @PutMapping("/{memberId}/settings/savings")
    public ApiResponse<MemberResponse> updateSavings(
        @PathVariable Long memberId,
        @RequestBody SavingsSettingRequest request
    ) {
        return ApiResponse.success(memberService.updateSavingsSettings(memberId, request));
    }

    @PutMapping("/{memberId}/onboarding")
    public ApiResponse<MemberResponse> onboarding(@PathVariable Long memberId) {
        return ApiResponse.success(memberService.completeOnboarding(memberId));
    }
}