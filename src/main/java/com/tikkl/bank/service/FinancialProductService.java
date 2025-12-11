package com.tikkl.bank.service;

import com.tikkl.bank.dto.request.FinancialProductRequest;
import com.tikkl.bank.dto.response.FinancialProductResponse;
import com.tikkl.bank.entity.FinancialProduct;
import com.tikkl.bank.entity.FinancialProduct.ProductType;
import com.tikkl.bank.exception.CustomException;
import com.tikkl.bank.exception.ErrorCode;
import com.tikkl.bank.repository.FinancialProductRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinancialProductService {

    private final FinancialProductRepository financialProductRepository;

    public List<FinancialProductResponse> getAllProducts() {
        return financialProductRepository.findByIsActiveTrue().stream()
            .map(FinancialProductResponse::from)
            .collect(Collectors.toList());
    }

    public List<FinancialProductResponse> getProductsByType(String productType) {
        FinancialProduct.ProductType type;
        try {
            type = FinancialProduct.ProductType.valueOf(productType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ProductException(ErrorCode.INVALID_REQUEST);
        }

        return financialProductRepository.findByProductTypeAndIsActiveTrue(type).stream()
            .map(FinancialProductResponse::from)
            .collect(Collectors.toList());
    }

    public FinancialProductResponse getProduct(Long productId) {
        FinancialProduct product = financialProductRepository.findById(productId)
            .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_NOT_FOUND));
        return FinancialProductResponse.from(product);
    }

    @Transactional
    public FinancialProductResponse createProduct(@Valid FinancialProductRequest request) {

        // 1. 제품 타입 변환 (문자열 → Enum)
        ProductType type;
        try {
            type = ProductType.valueOf(request.getProductType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ProductException(ErrorCode.INVALID_REQUEST);
        }

        // 2. 엔티티 생성
        FinancialProduct product = FinancialProduct.builder()
            .productName(request.getProductName())
            .productCode(request.getProductCode())
            .productType(type)
            .provider(request.getProvider())
            .interestRate(request.getInterestRate())
            .maxInterestRate(request.getMaxInterestRate())
            .minTerm(request.getMinTerm())
            .maxTerm(request.getMaxTerm())
            .minAmount(request.getMinAmount())
            .maxAmount(request.getMaxAmount())
            .description(request.getDescription())
            .terms(request.getTerms())
            .benefits(request.getBenefits())
            .build();

        // 3. 저장
        FinancialProduct saved = financialProductRepository.save(product);

        // 4. Response 변환해서 반환
        return FinancialProductResponse.from(saved);
    }

    public static class ProductException extends CustomException {

        public ProductException(ErrorCode errorCode) {
            super(errorCode);
        }
    }
}
