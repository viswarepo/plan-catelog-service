package com.sms.plan.dto;

import com.sms.plan.domain.BillingCycle;
import com.sms.plan.domain.CurrencyCode;
import com.sms.plan.domain.PlanStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class PlanDtos {

    private PlanDtos() {
    }

    public record PricePointRequest(
            @NotNull CurrencyCode currency,
            @NotNull BillingCycle billingCycle,
            @NotNull @Min(0) BigDecimal amount,
            @Min(0) int trialDays
    ) {
    }

    public record EntitlementRequest(
            @NotBlank String featureCode,
            @NotBlank String value
    ) {
    }

    public record CreatePlanRequest(
            @NotBlank String planCode,
            @NotBlank String name,
            String description,
            @NotEmpty @Valid List<PricePointRequest> pricePoints,
            @Valid List<EntitlementRequest> entitlements
    ) {
    }

    public record PricePointResponse(
            CurrencyCode currency,
            BillingCycle billingCycle,
            BigDecimal amount,
            int trialDays
    ) {
    }

    public record EntitlementResponse(
            String featureCode,
            String featureName,
            String value
    ) {
    }

    public record PlanResponse(
            String organizationId,
            String productCode,
            String planCode,
            int version,
            String name,
            String description,
            PlanStatus status
            //Instant effectiveFrom,
            //Instant effectiveTo
            //List<PricePointResponse> pricePoints,
            //List<EntitlementResponse> entitlements
    ) {

    }
}
