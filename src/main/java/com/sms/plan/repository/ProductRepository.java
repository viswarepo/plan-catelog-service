package com.sms.plan.repository;

import com.sms.plan.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByOrganizationIdAndProductCode(String organizationId, String productCode);
    boolean existsByOrganizationIdAndProductCode(String organizationId, String productCode);
    List<Product> findByOrganizationId(String organizationId);

}
