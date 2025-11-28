package com.tikkl.bank.service;

import com.tikkl.bank.dto.response.FinancialProductResponse;
import com.tikkl.bank.entity.FinancialProduct;
import com.tikkl.bank.exception.CustomException;
import com.tikkl.bank.exception.ErrorCode;
import com.tikkl.bank.repository.FinancialProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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

    public static class ProductException extends CustomException {
        public ProductException(ErrorCode errorCode) {
            super(errorCode);
        }
    }
}
