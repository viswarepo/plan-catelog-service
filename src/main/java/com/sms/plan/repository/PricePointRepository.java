package com.sms.plan.repository;

import com.sms.plan.domain.PricePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PricePointRepository extends JpaRepository<PricePoint, String> {
    @Query("SELECT pp FROM PricePoint pp WHERE pp.plan.id = :planId")
    PricePoint findByPlanId(@Param("planId") String planId);
}
