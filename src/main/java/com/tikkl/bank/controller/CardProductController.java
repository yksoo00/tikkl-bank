package com.tikkl.bank.controller;

import com.tikkl.bank.common.ApiResponse;
import com.tikkl.bank.dto.request.CardProductBenefitRequest;
import com.tikkl.bank.dto.request.CardProductRequest;
import com.tikkl.bank.dto.response.CardProductBenefitResponse;
import com.tikkl.bank.dto.response.CardProductResponse;
import com.tikkl.bank.service.CardProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card-products")
public class CardProductController {

    private final CardProductService cardProductService;

    /**
     * 1. 전체 마스터카드 조회
     */
    @GetMapping
    public ApiResponse<List<CardProductResponse>> getProducts() {
        return ApiResponse.success(cardProductService.getAllProducts());
    }

    /**
     * 2. 마스터카드 상세
     */
    @GetMapping("/{id}")
    public ApiResponse<CardProductResponse> getProduct(@PathVariable Long id) {
        return ApiResponse.success(cardProductService.getProduct(id));
    }

    /**
     * 3. 마스터카드 생성
     */
    @PostMapping
    public ApiResponse<CardProductResponse> createProduct(@RequestBody CardProductRequest request) {
        return ApiResponse.success(cardProductService.createProduct(request));
    }

    /**
     * 4. 마스터카드 혜택 추가
     */
    @PostMapping("/{id}/benefits")
    public ApiResponse<CardProductBenefitResponse> addBenefit(
        @PathVariable Long id,
        @RequestBody CardProductBenefitRequest request
    ) {
        return ApiResponse.success(cardProductService.addBenefit(id, request));
    }

    /**
     * 5. 마스터카드 혜택 조회
     */
    @GetMapping("/{id}/benefits")
    public ApiResponse<List<CardProductBenefitResponse>> getBenefits(@PathVariable Long id) {
        return ApiResponse.success(cardProductService.getBenefits(id));
    }
}