package com.tikkl.bank.controller;

import com.tikkl.bank.common.ApiResponse;
import com.tikkl.bank.dto.response.HomeResponse;
import com.tikkl.bank.dto.response.MyPageResponse;
import com.tikkl.bank.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/{memberId}/home")
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public ApiResponse<HomeResponse> home(@PathVariable Long memberId) {
        return ApiResponse.success(homeService.getHomeData(memberId));
    }

    @GetMapping("/mypage")
    public ApiResponse<MyPageResponse> myPage(@PathVariable Long memberId) {
        return ApiResponse.success(homeService.getMyPageData(memberId));
    }
}