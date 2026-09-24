package com.sms.plan.repository;

import com.sms.plan.domain.Plan;
import com.sms.plan.domain.PlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, String> {

    List<Plan> findByOrganizationIdAndPlanCodeOrderByVersionDesc(String organizationId, String planCode);

    Optional<Plan> findByOrganizationIdAndPlanCodeAndVersion(String organizationId, String planCode, int version);

    Optional<Plan> findByOrganizationIdAndPlanCodeAndStatus(String organizationId, String planCode, PlanStatus status);

    List<Plan> findByOrganizationIdAndStatus(String organizationId, PlanStatus status);

    List<Plan> findByOrganizationIdAndProduct_ProductCodeAndStatus(
            String organizationId, String productCode, PlanStatus status);

    @Query("""
        SELECT p FROM Plan p
        JOIN p.product pr
        WHERE pr.organizationId = :organizationId
    """)
    Optional<List<Plan>> findPlanByOrganizationId(@Param("organizationId") String organizationId);

    @Query("""
        SELECT p FROM Plan p
        JOIN p.product pr
        WHERE pr.organizationId = :organizationId and p.id=:id
    """)
    Plan findPlanByIdAndOrganizationId(@Param("id") String id,@Param("organizationId") String organizationId);

    @Query("SELECT p FROM Plan p " +
            "WHERE p.organizationId = :organizationId " +
            "AND p.product.productCode = :productCode")
    List<Plan> findPlanByOrganizationIdAndProduct(@Param("organizationId") String organizationId,@Param("productCode") String productCode);
}
