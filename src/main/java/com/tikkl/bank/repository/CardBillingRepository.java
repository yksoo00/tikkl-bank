package com.tikkl.bank.repository;

import com.tikkl.bank.entity.Card;
import com.tikkl.bank.entity.CardBilling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardBillingRepository extends JpaRepository<CardBilling, Long> {
    Optional<CardBilling> findByCardAndBillingYearAndBillingMonth(Card card, Integer year, Integer month);
    List<CardBilling> findByCard(Card card);
    List<CardBilling> findByCardOrderByBillingYearDescBillingMonthDesc(Card card);
    
    @Query("SELECT cb FROM CardBilling cb WHERE cb.card = :card AND cb.status = 'PENDING'")
    List<CardBilling> findPendingBillingsByCard(@Param("card") Card card);


    // ✅ 전월 전체 PENDING 청구 조회용
    List<CardBilling> findByBillingYearAndBillingMonthAndStatus(
        Integer billingYear,
        Integer billingMonth,
        CardBilling.BillingStatus status
    );
}
