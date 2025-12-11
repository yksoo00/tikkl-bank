// InterestHistoryRepository.java (새 파일)

package com.tikkl.bank.repository;

import com.tikkl.bank.entity.InterestHistory;
import com.tikkl.bank.entity.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InterestHistoryRepository extends JpaRepository<InterestHistory, Long> {
    List<InterestHistory> findBySavingsAccountAndInterestDateBetween(
        SavingsAccount savingsAccount, LocalDate start, LocalDate end);
}