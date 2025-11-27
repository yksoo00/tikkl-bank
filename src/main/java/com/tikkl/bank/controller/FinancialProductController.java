package com.tikkl.bank.controller;

import com.tikkl.bank.common.ApiResponse;
import com.tikkl.bank.dto.response.FinancialProductResponse;
import com.tikkl.bank.service.FinancialProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class FinancialProductController {

    private final FinancialProductService financialProductService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FinancialProductResponse>>> getAllProducts() {
        List<FinancialProductResponse> response = financialProductService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/type/{productType}")
    public ResponseEntity<ApiResponse<List<FinancialProductResponse>>> getProductsByType(
            @PathVariable String productType) {
        List<FinancialProductResponse> response = financialProductService.getProductsByType(productType);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<FinancialProductResponse>> getProduct(@PathVariable Long productId) {
        FinancialProductResponse response = financialProductService.getProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
