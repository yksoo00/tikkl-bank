package com.tikkl.bank.repository;

import com.tikkl.bank.entity.Member;
import com.tikkl.bank.entity.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {
    Optional<SavingsAccount> findByMember(Member member);
    Optional<SavingsAccount> findByAccountNumber(String accountNumber);
    boolean existsByMember(Member member);
}
