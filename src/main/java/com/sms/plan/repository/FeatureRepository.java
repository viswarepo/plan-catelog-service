package com.sms.plan.repository;

import com.sms.plan.domain.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeatureRepository extends JpaRepository<Feature, Long> {
    Optional<Feature> findByOrganizationIdAndCode(String organizationId, String code);
    boolean existsByOrganizationIdAndCode(String organizationId, String code);
    List<Feature> findByOrganizationId(String organizationId);
}
