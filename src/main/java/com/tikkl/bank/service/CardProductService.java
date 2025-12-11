package com.tikkl.bank.service;

import com.tikkl.bank.dto.request.CardProductBenefitRequest;
import com.tikkl.bank.dto.request.CardProductRequest;
import com.tikkl.bank.dto.response.CardProductBenefitResponse;
import com.tikkl.bank.dto.response.CardProductResponse;
import com.tikkl.bank.entity.CardProduct;
import com.tikkl.bank.entity.CardProductBenefit;
import com.tikkl.bank.exception.CustomException;
import com.tikkl.bank.exception.ErrorCode;
import com.tikkl.bank.repository.CardProductBenefitRepository;
import com.tikkl.bank.repository.CardProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardProductService {

    private final CardProductRepository cardProductRepository;
    private final CardProductBenefitRepository benefitRepository;

    /**
     * 1. 전체 마스터 카드 조회
     */
    public List<CardProductResponse> getAllProducts() {
        return cardProductRepository.findAll().stream()
            .map(CardProductResponse::from)
            .collect(Collectors.toList());
    }

    /**
     * 2. 마스터 카드 상세 조회 - 컨트롤러에서 ApiResponse<CardProductResponse> 로 받으니까 여기서도 CardProductResponse 를
     * 리턴해야 함
     */
    public CardProductResponse getProduct(Long productId) {
        CardProduct product = cardProductRepository.findById(productId)
            .orElseThrow(() -> new CustomException(ErrorCode.CARD_PRODUCT_NOT_FOUND));

        return CardProductResponse.from(product);
    }

    /**
     * 3. 마스터 카드 등록
     */
    public CardProductResponse createProduct(CardProductRequest request) {

        CardProduct product = CardProduct.builder()
            .name(request.getName())
            .company(request.getCompany())
            .cardType(request.getCardType())          // <- 네가 enum으로 추가해 둔 필드 사용
            .annualFee(request.getAnnualFee())
            .description(request.getDescription())
            .imageUrl(request.getImageUrl())
            .summaryBenefits(request.getSummaryBenefits())
            .active(true)
            .build();

        cardProductRepository.save(product);

        // 혜택 저장
        if (request.getBenefits() != null) {
            request.getBenefits().forEach(b -> {
                CardProductBenefit benefit = CardProductBenefit.builder()
                    .cardProduct(product)
                    .benefitName(b.getBenefitName())
                    .benefitType(b.getBenefitType())
                    .categoryCode(b.getCategoryCode())
                    .discountRate(b.getDiscountRate())
                    .maxDiscountPerMonth(b.getMaxDiscountPerMonth())
                    .minSpendingForActivation(b.getMinSpendingForActivation())
                    .description(b.getDescription())
                    .active(true)
                    .build();
                benefitRepository.save(benefit);
            });
        }

        return CardProductResponse.from(product);
    }

    /**
     * 4. 마스터 카드 혜택 추가 - /card-products/{id}/benefits  POST
     */
    public CardProductBenefitResponse addBenefit(Long productId,
        CardProductBenefitRequest request) {

        CardProduct product = cardProductRepository.findById(productId)
            .orElseThrow(() -> new CustomException(ErrorCode.CARD_PRODUCT_NOT_FOUND));

        CardProductBenefit benefit = CardProductBenefit.builder()
            .cardProduct(product)
            .benefitName(request.getBenefitName())
            .benefitType(request.getBenefitType())
            .categoryCode(request.getCategoryCode())
            .discountRate(request.getDiscountRate())
            .maxDiscountPerMonth(request.getMaxDiscountPerMonth())
            .minSpendingForActivation(request.getMinSpendingForActivation())
            .description(request.getDescription())
            .active(true)
            .build();

        benefitRepository.save(benefit);

        return CardProductBenefitResponse.from(benefit);
    }

    /**
     * 5. 마스터 카드 혜택 목록 조회 - /card-products/{id}/benefits  GET
     */
    public List<CardProductBenefitResponse> getBenefits(Long productId) {

        CardProduct product = cardProductRepository.findById(productId)
            .orElseThrow(() -> new CustomException(ErrorCode.CARD_PRODUCT_NOT_FOUND));

        List<CardProductBenefit> benefits = benefitRepository.findByCardProduct(product);

        return benefits.stream()
            .map(CardProductBenefitResponse::from)
            .collect(Collectors.toList());
    }
}