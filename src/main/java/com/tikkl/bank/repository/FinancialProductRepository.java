package com.tikkl.bank.repository;

import com.tikkl.bank.entity.FinancialProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialProductRepository extends JpaRepository<FinancialProduct, Long> {
    List<FinancialProduct> findByIsActiveTrue();
    List<FinancialProduct> findByProductTypeAndIsActiveTrue(FinancialProduct.ProductType productType);
    Optional<FinancialProduct> findByProductCode(String productCode);
}
