package com.tikkl.bank.repository;

import com.tikkl.bank.entity.Card;
import com.tikkl.bank.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByMemberAndIsActiveTrue(Member member);
    List<Card> findByMember(Member member);
}
