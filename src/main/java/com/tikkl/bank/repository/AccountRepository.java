package com.tikkl.bank.repository;

import com.tikkl.bank.entity.Account;
import com.tikkl.bank.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByMemberAndIsActiveTrue(Member member);
    Optional<Account> findByMemberAndIsPrimaryTrue(Member member);
    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}
