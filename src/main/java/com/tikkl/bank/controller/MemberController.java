package com.tikkl.bank.controller;

import com.tikkl.bank.common.ApiResponse;
import com.tikkl.bank.dto.request.SavingsSettingRequest;
import com.tikkl.bank.dto.response.MemberResponse;
import com.tikkl.bank.dto.response.MyPageResponse;
import com.tikkl.bank.service.HomeService;
import com.tikkl.bank.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final HomeService homeService;

    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMember(@PathVariable Long memberId) {
        MemberResponse response = memberService.getMember(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{memberId}/mypage")
    public ResponseEntity<ApiResponse<MyPageResponse>> getMyPage(@PathVariable Long memberId) {
        MyPageResponse response = homeService.getMyPageData(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{memberId}/savings-settings")
    public ResponseEntity<ApiResponse<MemberResponse>> updateSavingsSettings(
            @PathVariable Long memberId,
            @Valid @RequestBody SavingsSettingRequest request) {
        MemberResponse response = memberService.updateSavingsSettings(memberId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "저축 설정이 업데이트되었습니다"));
    }

    @PostMapping("/{memberId}/onboarding")
    public ResponseEntity<ApiResponse<MemberResponse>> completeOnboarding(@PathVariable Long memberId) {
        MemberResponse response = memberService.completeOnboarding(memberId);
        return ResponseEntity.ok(ApiResponse.success(response, "온보딩이 완료되었습니다"));
    }
}
