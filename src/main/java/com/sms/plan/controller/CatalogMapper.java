package com.sms.plan.controller;

import com.sms.plan.domain.*;
import com.sms.plan.dto.FeatureDtos.FeatureResponse;
import com.sms.plan.dto.PlanDtos.EntitlementResponse;
import com.sms.plan.dto.PlanDtos.PlanResponse;
import com.sms.plan.dto.PlanDtos.PricePointResponse;
import com.sms.plan.dto.ProductDtos.ProductResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CatalogMapper {

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getOrganizationId(),
                product.getProductCode(),
                product.getName(),
                product.getDescription(),
                product.isActive()
        );
    }

    public PlanResponse toResponse(Plan plan) {
        List<PricePointResponse> prices = plan.getPricePoints().stream()
                .map(this::toResponse)
                .toList();

        List<EntitlementResponse> entitlements = plan.getEntitlements().stream()
                .map(this::toResponse)
                .toList();

        return new PlanResponse(
                plan.getOrganizationId(),
                plan.getProduct().getProductCode(),
                plan.getPlanCode(),
                plan.getVersion(),
                plan.getName(),
                plan.getDescription(),
                plan.getStatus()
                //plan.getEffectiveFrom(),
                //plan.getEffectiveTo(),
                //plan.getPricePoints(),
                //plan.getEntitlements()
        );
    }

    public PricePointResponse toResponse(PricePoint pricePoint) {
        return new PricePointResponse(
                pricePoint.getCurrency(),
                pricePoint.getBillingCycle(),
                pricePoint.getAmount(),
                pricePoint.getTrialDays()
        );
    }

    public EntitlementResponse toResponse(PlanEntitlement entitlement) {
        return new EntitlementResponse(
                entitlement.getFeature().getCode(),
                entitlement.getFeature().getName(),
                entitlement.getValue()
        );
    }

    public FeatureResponse toResponse(Feature feature) {
        return new FeatureResponse(
                feature.getOrganizationId(),
                feature.getCode(),
                feature.getName(),
                feature.getDescription(),
                feature.getType()
        );
    }
}
