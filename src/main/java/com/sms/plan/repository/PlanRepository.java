package com.sms.plan.repository;

import com.sms.plan.domain.Plan;
import com.sms.plan.domain.PlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

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
    List<Plan> findPlanByOrganizationId(@Param("organizationId") String organizationId);
}
