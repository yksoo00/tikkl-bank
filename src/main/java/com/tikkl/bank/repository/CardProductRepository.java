package com.tikkl.bank.repository;

import com.tikkl.bank.entity.CardProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardProductRepository extends JpaRepository<CardProduct, Long> {
    // 필요하면 여기다가 findByName 같은 메서드 추가 가능
}