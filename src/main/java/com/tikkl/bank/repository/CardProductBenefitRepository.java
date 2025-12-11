package com.tikkl.bank.repository;

import com.tikkl.bank.entity.CardProduct;
import com.tikkl.bank.entity.CardProductBenefit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardProductBenefitRepository extends JpaRepository<CardProductBenefit, Long> {

    // 카드 상품에 연결된 혜택 구간들 가져오기
    List<CardProductBenefit> findByCardProduct(CardProduct cardProduct);
}