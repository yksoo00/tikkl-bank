package com.tikkl.bank.repository;

import com.tikkl.bank.entity.Member;
import com.tikkl.bank.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findTop10ByMemberOrderByTransactionAtDesc(Member member);
    
    Page<Transaction> findByMember(Member member, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.member = :member " +
           "AND (:transactionType IS NULL OR t.transactionType = :transactionType) " +
           "AND (:category IS NULL OR t.category = :category) " +
           "AND (:startDate IS NULL OR t.transactionAt >= :startDate) " +
           "AND (:endDate IS NULL OR t.transactionAt <= :endDate) " +
           "AND (:keyword IS NULL OR t.description LIKE %:keyword% OR t.merchant LIKE %:keyword%) " +
           "ORDER BY t.transactionAt DESC")
    Page<Transaction> searchTransactions(
            @Param("member") Member member,
            @Param("transactionType") Transaction.TransactionType transactionType,
            @Param("category") String category,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("keyword") String keyword,
            Pageable pageable);
}
