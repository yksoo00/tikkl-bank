package com.tikkl.bank.controller;

import com.tikkl.bank.common.ApiResponse;
import com.tikkl.bank.service.CardService;
import com.tikkl.bank.service.SavingsService;
import com.tikkl.bank.service.SimulationDateService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class SimulationController {

    private final SavingsService savingsService;
    private final CardService cardService;
    private final SimulationDateService simulationDateService;


    /**
     * 하루 지나간 것처럼 이자 적립 한 번 실행
     */
    @PostMapping("/simulate/day")
    public ApiResponse<Void> simulateDay() {

        // 1. 날짜 1일 증가
        LocalDate newDate = simulationDateService.plusOneDay();

        // 2. 그 날짜 기준으로 이자 적용
        savingsService.applyDailyInterest(newDate);

        return ApiResponse.success(null, "하루 경과! 적용 날짜 = " + newDate);
    }

    /**
     * 월말 결제 로직 한 번 실행
     */
    @PostMapping("/simulate/month-end")
    public ApiResponse<Void> simulateMonthEnd() {
        cardService.payLastMonthBillsFromTikkl();    // 위에서 만든 메서드
        return ApiResponse.success(null);
    }
}