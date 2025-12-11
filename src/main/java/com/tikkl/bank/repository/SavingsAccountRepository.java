package com.tikkl.bank.repository;

import com.tikkl.bank.entity.Member;
import com.tikkl.bank.entity.SavingsAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {

    Optional<SavingsAccount> findByMember(Member member);

    Optional<SavingsAccount> findByAccountNumber(String accountNumber);

    boolean existsByMember(Member member);

    Optional<SavingsAccount> findByMemberAndIsActiveTrue(Member member);

    // ✅ 이자 정산 대상 계좌 조회용
    List<SavingsAccount> findByIsActiveTrue();
}
