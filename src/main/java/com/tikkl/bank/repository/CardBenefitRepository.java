package com.tikkl.bank.repository;

import com.tikkl.bank.entity.Card;
import com.tikkl.bank.entity.CardBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CardBenefitRepository extends JpaRepository<CardBenefit, Long> {
    List<CardBenefit> findByCardAndIsActiveTrue(Card card);
    List<CardBenefit> findByCard(Card card);
    List<CardBenefit> findByCardAndCategory(Card card, String category);
}
